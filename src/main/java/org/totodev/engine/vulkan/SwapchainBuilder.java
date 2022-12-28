package org.totodev.engine.vulkan;

import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.util.StackAlloc;

import java.nio.*;
import java.util.Arrays;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class SwapchainBuilder {
    private VkDevice device;
    private long surface;
    private int imageCount;

    private int sizeX;
    private int sizeY;

    private int format = VK_FORMAT_B8G8R8A8_SRGB;
    private int colorSpace = VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
    private boolean formatAllowFallback = true;

    private int presentMode = VK_PRESENT_MODE_FIFO_KHR;
    private boolean presentModeAllowFallback = true;

    private int imageLayers = 1;
    private int imageUsage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;

    private int[] queueIndices;

    private boolean clipped;

    public SwapchainBuilder device(VkDevice device) {
        this.device = device;
        return this;
    }

    public SwapchainBuilder surface(long surface) {
        this.surface = surface;
        return this;
    }

    public SwapchainBuilder imageCount(int imageCount) {
        this.imageCount = imageCount;
        return this;
    }

    public SwapchainBuilder format(int format, int colorSpace, boolean allowFallback) {
        this.format = format;
        this.colorSpace = colorSpace;
        this.formatAllowFallback = allowFallback;
        return this;
    }

    public SwapchainBuilder presentMode(int presentMode, boolean allowFallback) {
        this.presentMode = presentMode;
        this.presentModeAllowFallback = allowFallback;
        return this;
    }

    public SwapchainBuilder imageLayers(int imageLayers) {
        this.imageLayers = imageLayers;
        return this;
    }

    public SwapchainBuilder imageUsage(int imageUsage) {
        this.imageUsage = imageUsage;
        return this;
    }

    /**
     * Only effective if the window manager allows the swapchain size to differ from the window size
     */
    public SwapchainBuilder size(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        return this;
    }

    public SwapchainBuilder queues(QueueFamily... queueFamilies) {
        int[] indices = new int[queueFamilies.length];
        for (int i = 0; i < indices.length; i++) indices[i] = queueFamilies[i].familyIndex();
        this.queueIndices = Arrays.stream(indices).distinct().toArray();
        return this;
    }
    public SwapchainBuilder queues(int... queueIndices) {
        this.queueIndices = Arrays.stream(queueIndices).distinct().toArray();
        return this;
    }

    public SwapchainBuilder clipped(boolean clipped) {
        this.clipped = clipped;
        return this;
    }

    public record SwapchainCreationResult(long swapchain, long[] images, int imageFormat, VkExtent2D extent) {
    }
    public SwapchainCreationResult build() {
        try (MemoryStack stack = stackPush()) {
            // ==== Choose settings ====
            SwapchainSupportDetails swapChainSupport = querySupport();

            VkSurfaceFormatKHR format = chooseSurfaceFormat(swapChainSupport.formats);
            int presentMode = choosePresentMode(swapChainSupport.presentModes);
            VkExtent2D extent = chooseExtent(swapChainSupport.capabilities);

            if (swapChainSupport.capabilities.maxImageCount() == 0)
                imageCount = org.joml.Math.clamp(swapChainSupport.capabilities.minImageCount(), Integer.MAX_VALUE, imageCount);
            else
                imageCount = org.joml.Math.clamp(swapChainSupport.capabilities.minImageCount(), swapChainSupport.capabilities.maxImageCount(), imageCount);
            IntBuffer pImageCount = stack.ints(imageCount);

            // ==== Create info ====
            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(surface);
            // Image settings
            createInfo.minImageCount(imageCount);
            createInfo.imageFormat(format.format());
            createInfo.imageColorSpace(format.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(imageLayers);
            createInfo.imageUsage(imageUsage);

            if (queueIndices == null || queueIndices.length <= 1) {
                createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            } else {
                createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(queueIndices));
            }

            createInfo.preTransform(swapChainSupport.capabilities.currentTransform()); //TODO: Configurable swapchain transform?
            createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR); //TODO: Configurable alpha blending mode
            createInfo.presentMode(presentMode);
            createInfo.clipped(clipped);

            createInfo.oldSwapchain(VK_NULL_HANDLE); //TODO: Swapchain creation from old swapchain

            // ==== Collect results ====
            LongBuffer pSwapChain = stack.longs(VK_NULL_HANDLE);
            if (vkCreateSwapchainKHR(device, createInfo, null, pSwapChain) != VK_SUCCESS)
                throw new RuntimeException("Failed to create swapchain");
            long swapchain = pSwapChain.get(0);

            LongBuffer pSwapchainImages = stack.mallocLong(imageCount);
            vkGetSwapchainImagesKHR(device, swapchain, pImageCount, null);
            vkGetSwapchainImagesKHR(device, swapchain, pImageCount, pSwapchainImages);

            long[] swapchainImages = new long[pSwapchainImages.capacity()];
            for (int i = 0; i < swapchainImages.length; i++)
                swapchainImages[i] = pSwapchainImages.get(i);

            return new SwapchainCreationResult(swapchain, swapchainImages, format.format(), VkExtent2D.create().set(extent));
        }
    }

    public record SwapchainSupportDetails(VkSurfaceCapabilitiesKHR capabilities, VkSurfaceFormatKHR.Buffer formats, IntBuffer presentModes) {
    }
    public @StackAlloc SwapchainSupportDetails querySupport() {
        MemoryStack stack = stackGet();

        VkSurfaceCapabilitiesKHR capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device.getPhysicalDevice(), surface, capabilities);

        IntBuffer count = stack.ints(0);
        vkGetPhysicalDeviceSurfaceFormatsKHR(device.getPhysicalDevice(), surface, count, null);

        VkSurfaceFormatKHR.Buffer formats = null;
        if (count.get(0) != 0) {
            formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device.getPhysicalDevice(), surface, count, formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device.getPhysicalDevice(), surface, count, null);

        IntBuffer presentModes = null;
        if (count.get(0) != 0) {
            presentModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device.getPhysicalDevice(), surface, count, presentModes);
        }

        return new SwapchainSupportDetails(capabilities, formats, presentModes);
    }

    private VkSurfaceFormatKHR chooseSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        return availableFormats.stream()
            .filter(availableFormat -> availableFormat.format() == format)
            .filter(availableFormat -> availableFormat.colorSpace() == colorSpace)
            .findAny()
            .orElseGet(() -> {
                if (formatAllowFallback) return availableFormats.get(0);
                else
                    throw new RuntimeException("Swapchain format " + format + " with color space " + colorSpace + " not supported.");
            });
    }

    private int choosePresentMode(IntBuffer availableModes) {
        while (availableModes.hasRemaining()) {
            int mode = availableModes.get();
            if (mode == presentMode) return presentMode;
        }

        if (presentModeAllowFallback) return VK_PRESENT_MODE_FIFO_KHR;
        else throw new RuntimeException("Swapchain present mode " + presentMode + " not supported.");
    }

    private @StackAlloc VkExtent2D chooseExtent(VkSurfaceCapabilitiesKHR capabilities) {
        if (capabilities.currentExtent().width() != 0xFFFFFFFF)
            return capabilities.currentExtent();

        MemoryStack stack = stackGet();

        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();

        return VkExtent2D.malloc(stack).set(
            org.joml.Math.clamp(minExtent.width(), maxExtent.width(), sizeX),
            org.joml.Math.clamp(minExtent.height(), maxExtent.height(), sizeY)
        );
    }
}
