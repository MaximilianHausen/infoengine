package org.totodev.engine.resources.image;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.totodev.engine.core.Engine;
import org.totodev.engine.rendering.Image;
import org.totodev.engine.rendering.vulkan.*;
import org.totodev.engine.resources.Resource;
import org.totodev.engine.util.IO;

import java.io.File;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class ImageResource implements Resource, ImageProvider {
    private final File file;
    private VkImageHelper.VkImage image;
    private long imageView;
    private long sampler;

    private int width = 0;
    private int height = 0;

    private boolean loading = false;

    public ImageResource(File file) {
        if (!file.isFile())
            throw new IllegalArgumentException("File " + file + " is not a file");
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    public VkImageHelper.VkImage getImage() {
        if (loading) return getEmptyImage();
        if (!isLoaded()) {
            load();
            return getEmptyImage();
        }
        return image;
    }

    public long getImageView() {
        if (loading) return getEmptyImageView();
        if (!isLoaded()) {
            load();
            return getEmptyImageView();
        }
        return imageView;
    }

    public long getSampler() {
        if (loading) return getEmptySampler();
        if (!isLoaded()) {
            load();
            return getEmptySampler();
        }
        return sampler;
    }

    public boolean isLoaded() {
        return image != null;
    }

    public void load() {
        if (isLoaded()) return;
        Image temp = IO.loadImageFromFile(file);
        loading = true;
        Engine.executeOnWorkerPool(r -> {
            image = VkImageHelper.createTextureImage(r.commandPool(), temp);
            imageView = VkImageHelper.createImageView(image.image(), VK_FORMAT_R8G8B8A8_SRGB);
            sampler = VkImageHelper.createTextureSampler(VK_FILTER_NEAREST, VK_FILTER_NEAREST, VK_SAMPLER_ADDRESS_MODE_REPEAT, 0);
            width = temp.getWidth();
            height = temp.getHeight();
            loading = false;
            temp.close();
        });
    }

    public void unload() {
        if (!isLoaded()) return;
        vkDestroySampler(Engine.getLogicalDevice(), sampler, null);
        vkDestroyImageView(Engine.getLogicalDevice(), imageView, null);
        vkDestroyImage(Engine.getLogicalDevice(), image.image(), null);
        vkFreeMemory(Engine.getLogicalDevice(), image.imageMemory(), null);
        sampler = 0;
        imageView = 0;
        image = null;
    }

    //region Empty
    private static VkImageHelper.VkImage emptyImage;
    private static long emptyImageView;
    private static long emptySampler;

    private static VkImageHelper.VkImage getEmptyImage() {
        if (emptyImage == null) loadEmpty();
        return emptyImage;
    }

    private static long getEmptyImageView() {
        if (emptyImage == null) loadEmpty();
        return emptyImageView;
    }

    private static long getEmptySampler() {
        if (emptyImage == null) loadEmpty();
        return emptySampler;
    }

    public static void loadEmpty() {
        try (MemoryStack stack = stackPush()) {
            try (Image temp = new Image(stack.calloc(4), 1, 1, 4)) {
                long commandPool = VkCommandBufferHelper.createCommandPool(0, Engine.getGraphicsQueueFamily().familyIndex());
                emptyImage = VkImageHelper.createTextureImage(commandPool, temp);
                emptyImageView = VkImageHelper.createImageView(emptyImage.image(), VK_FORMAT_R8G8B8A8_SRGB);
                emptySampler = VkImageHelper.createTextureSampler(VK_FILTER_NEAREST, VK_FILTER_NEAREST, VK_SAMPLER_ADDRESS_MODE_REPEAT, 0);
            }
        }
    }
    //endregion
}
