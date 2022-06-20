package net.totodev.infoengine.resources.image;

import net.totodev.infoengine.core.Engine;
import net.totodev.infoengine.rendering.Image;
import net.totodev.infoengine.rendering.vulkan.VkImageHelper;
import net.totodev.infoengine.util.IO;
import org.lwjgl.system.MemoryStack;

import java.io.File;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class ImageResource {
    static {
        try (MemoryStack stack = stackPush()) {
            try (Image temp = new Image(stack.calloc(4 * Integer.BYTES), 1, 1, 4)) {
                Engine.executeOnWorkerPool(r -> emptyImage = VkImageHelper.createTextureImage(r.commandPool(), temp));
            }
        }
    }

    private static VkImageHelper.VkImage emptyImage;

    private final File file;
    private VkImageHelper.VkImage image;
    private boolean loading = false;

    public ImageResource(File file) {
        if (!file.isFile())
            throw new IllegalArgumentException("File " + file + " is not a file");
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public VkImageHelper.VkImage getImage() {
        if (loading) return emptyImage;
        return image;
    }

    public boolean isLoaded() {
        return image != null;
    }

    public boolean isLoading() {
        return loading;
    }

    public void load() {
        if (loading || isLoaded()) return;
        loading = true;
        try (Image temp = IO.loadImageFromFile(file)) {
            Engine.executeOnWorkerPool(r -> {
                image = VkImageHelper.createTextureImage(r.commandPool(), temp);
                loading = false;
            });
        }
    }

    public void unload() {
        if (loading || !isLoaded()) return;
        vkDestroyImage(Engine.getLogicalDevice(), image.image(), null);
        vkFreeMemory(Engine.getLogicalDevice(), image.imageMemory(), null);
        image = null;
    }
}
