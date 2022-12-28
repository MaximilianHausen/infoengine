package org.totodev.engine.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class ImageViewBuilder {
    private VkDevice device;
    private long[] images;
    private int viewType = VK_IMAGE_VIEW_TYPE_2D;
    private int imageFormat = VK_FORMAT_B8G8R8A8_SRGB;

    private int aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
    private int mipBase = 0, mipCount = 1;
    private int layerBase = 0, layerCount = 1;

    private int swizzleR = VK_COMPONENT_SWIZZLE_IDENTITY,
        swizzleG = VK_COMPONENT_SWIZZLE_IDENTITY,
        swizzleB = VK_COMPONENT_SWIZZLE_IDENTITY,
        swizzleA = VK_COMPONENT_SWIZZLE_IDENTITY;

    public ImageViewBuilder device(VkDevice device) {
        this.device = device;
        return this;
    }

    public ImageViewBuilder image(long image) {
        this.images = new long[]{image};
        return this;
    }
    public ImageViewBuilder images(long... images) {
        this.images = images;
        return this;
    }

    public ImageViewBuilder viewType(int viewType) {
        this.viewType = viewType;
        return this;
    }

    public ImageViewBuilder imageFormat(int format) {
        this.imageFormat = format;
        return this;
    }

    public ImageViewBuilder aspectMask(int aspectMask) {
        this.aspectMask = aspectMask;
        return this;
    }

    public ImageViewBuilder mipLevels(int base, int count) {
        this.mipBase = base;
        this.mipCount = count;
        return this;
    }

    public ImageViewBuilder layers(int base, int count) {
        this.layerBase = base;
        this.layerCount = count;
        return this;
    }

    public ImageViewBuilder swizzle(int r, int g, int b, int a) {
        this.swizzleR = r;
        this.swizzleG = g;
        this.swizzleB = b;
        this.swizzleA = a;
        return this;
    }

    public long buildOne() {
        if (images == null || images.length == 0) throw new RuntimeException("No image specified");
        try (MemoryStack stack = stackPush()) {
            VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            createInfo.image(images[0]);
            createInfo.viewType(viewType);
            createInfo.format(imageFormat);

            createInfo.components().r(swizzleR);
            createInfo.components().g(swizzleG);
            createInfo.components().b(swizzleB);
            createInfo.components().a(swizzleA);

            createInfo.subresourceRange().aspectMask(aspectMask);
            createInfo.subresourceRange().baseMipLevel(mipBase);
            createInfo.subresourceRange().levelCount(mipCount);
            createInfo.subresourceRange().baseArrayLayer(layerBase);
            createInfo.subresourceRange().layerCount(layerCount);

            LongBuffer pImageView = stack.mallocLong(1);
            if (vkCreateImageView(device, createInfo, null, pImageView) != VK_SUCCESS)
                throw new RuntimeException("Failed to create image view");

            return pImageView.get(0);
        }
    }

    public long[] buildAll() {
        try (MemoryStack stack = stackPush()) {
            VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            createInfo.viewType(viewType);
            createInfo.format(imageFormat);

            createInfo.components().r(swizzleR);
            createInfo.components().g(swizzleG);
            createInfo.components().b(swizzleB);
            createInfo.components().a(swizzleA);

            createInfo.subresourceRange().aspectMask(aspectMask);
            createInfo.subresourceRange().baseMipLevel(mipBase);
            createInfo.subresourceRange().levelCount(mipCount);
            createInfo.subresourceRange().baseArrayLayer(layerBase);
            createInfo.subresourceRange().layerCount(layerCount);

            long[] imageViews = new long[images.length];

            LongBuffer pImageView = stack.mallocLong(1);
            for (int i = 0; i < images.length; i++) {
                createInfo.image(images[i]);

                if (vkCreateImageView(device, createInfo, null, pImageView) != VK_SUCCESS)
                    throw new RuntimeException("Failed to create image views");

                imageViews[i] = pImageView.get(0);
            }

            return imageViews;
        }
    }
}
