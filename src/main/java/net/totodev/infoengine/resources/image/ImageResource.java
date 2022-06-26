package net.totodev.infoengine.resources.image;

import net.totodev.infoengine.core.Engine;
import net.totodev.infoengine.rendering.Image;
import net.totodev.infoengine.rendering.vulkan.*;
import net.totodev.infoengine.resources.Resource;
import net.totodev.infoengine.util.IO;

import java.io.File;

import static org.lwjgl.vulkan.VK10.*;

public class ImageResource implements Resource, ImageProvider {
    private final File file;
    private VkImageHelper.VkImage image;
    private long imageView;
    private long sampler;

    public ImageResource(File file) {
        if (!file.isFile())
            throw new IllegalArgumentException("File " + file + " is not a file");
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public VkImageHelper.VkImage getImage() {
        if (!isLoaded()) load();
        return image;
    }

    public long getImageView() {
        if (!isLoaded()) load();
        return imageView;
    }

    public long getSampler() {
        if (!isLoaded()) load();
        return sampler;
    }

    public boolean isLoaded() {
        return image != null;
    }

    public void load() {
        if (isLoaded()) return;
        try (Image temp = IO.loadImageFromFile(file)) {
            //Engine.executeOnWorkerPool(r -> {
            //FIXME
                long commandPool = VkCommandBufferHelper.createCommandPool(0, Engine.getGraphicsQueueFamily());
                image = VkImageHelper.createTextureImage(commandPool, temp);
                imageView = VkImageHelper.createImageView(image.image(), VK_FORMAT_R8G8B8A8_SRGB);
                sampler = VkImageHelper.createTextureSampler(VK_FILTER_NEAREST, VK_FILTER_NEAREST, VK_SAMPLER_ADDRESS_MODE_REPEAT, 0);
            //});
        }
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
}
