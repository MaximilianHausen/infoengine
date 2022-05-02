package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.*;
import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public final class VkSwapchainHelper {
    public static class SwapchainSupportDetails {
        public VkSurfaceCapabilitiesKHR capabilities;
        public VkSurfaceFormatKHR.Buffer formats;
        public IntBuffer presentModes;
    }

    public record SwapchainCreationResult(long swapchain, LongList images, int imageFormat, VkExtent2D extent) {
    }

    public static SwapchainSupportDetails querySwapChainSupport(VkPhysicalDevice device, long surface) {
        MemoryStack stack = stackGet();
        SwapchainSupportDetails details = new SwapchainSupportDetails();

        details.capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, details.capabilities);

        IntBuffer count = stack.ints(0);
        vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, null);

        if (count.get(0) != 0) {
            details.formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, details.formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, null);

        if (count.get(0) != 0) {
            details.presentModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, details.presentModes);
        }

        return details;
    }

    public static LongList createImageViews(LongList swapchainImages, int swapchainImageFormat) {
        MutableLongList imageViews = LongLists.mutable.empty();

        try (MemoryStack stack = stackPush()) {
            LongBuffer pImageView = stack.mallocLong(1);

            swapchainImages.forEach(swapchainImage -> {
                VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack);

                createInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
                createInfo.image(swapchainImage);
                createInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
                createInfo.format(swapchainImageFormat);

                createInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().g(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().b(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().a(VK_COMPONENT_SWIZZLE_IDENTITY);

                createInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                createInfo.subresourceRange().baseMipLevel(0);
                createInfo.subresourceRange().levelCount(1);
                createInfo.subresourceRange().baseArrayLayer(0);
                createInfo.subresourceRange().layerCount(1);

                if (vkCreateImageView(Engine.getLogicalDevice(), createInfo, null, pImageView) != VK_SUCCESS)
                    throw new RuntimeException("Failed to create image views");

                imageViews.add(pImageView.get(0));
            });
        }

        return imageViews;
    }

    public static SwapchainCreationResult createSwapChain(VkDevice logicalDevice, Window window) {
        try (MemoryStack stack = stackPush()) {
            SwapchainSupportDetails swapChainSupport = querySwapChainSupport(logicalDevice.getPhysicalDevice(), window.getVkSurface());

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(swapChainSupport.formats);
            int presentMode = chooseSwapPresentMode(swapChainSupport.presentModes);
            VkExtent2D extent = chooseSwapExtent(swapChainSupport.capabilities, window.getSize().x, window.getSize().y);

            IntBuffer imageCount = stack.ints(swapChainSupport.capabilities.minImageCount() + 1);

            if (swapChainSupport.capabilities.maxImageCount() > 0 && imageCount.get(0) > swapChainSupport.capabilities.maxImageCount())
                imageCount.put(0, swapChainSupport.capabilities.maxImageCount());

            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(window.getVkSurface());
            // Image settings
            createInfo.minImageCount(imageCount.get(0));
            createInfo.imageFormat(surfaceFormat.format());
            createInfo.imageColorSpace(surfaceFormat.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(1);
            createInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            VkQueueHelper.QueueFamilyIndices indices = VkQueueHelper.findQueueFamilies(logicalDevice.getPhysicalDevice(), window.getVkSurface());

            if (!indices.graphicsFamily.equals(indices.presentFamily)) {
                createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(indices.graphicsFamily, indices.presentFamily));
            } else {
                createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }

            createInfo.preTransform(swapChainSupport.capabilities.currentTransform());
            createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR); //TODO: Transparent framebuffer here
            createInfo.presentMode(presentMode);
            createInfo.clipped(true);

            createInfo.oldSwapchain(VK_NULL_HANDLE);

            LongBuffer pSwapChain = stack.longs(VK_NULL_HANDLE);
            if (vkCreateSwapchainKHR(logicalDevice, createInfo, null, pSwapChain) != VK_SUCCESS)
                throw new RuntimeException("Failed to create swap chain");
            long swapchain = pSwapChain.get(0);

            vkGetSwapchainImagesKHR(logicalDevice, swapchain, imageCount, null);
            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));
            vkGetSwapchainImagesKHR(logicalDevice, swapchain, imageCount, pSwapchainImages);
            MutableLongList swapchainImages = LongLists.mutable.of(imageCount.get(0));

            for (int i = 0; i < pSwapchainImages.capacity(); i++)
                swapchainImages.add(pSwapchainImages.get(i));

            int swapchainImageFormat = surfaceFormat.format();
            VkExtent2D swapchainExtent = VkExtent2D.create().set(extent);

            return new SwapchainCreationResult(swapchain, swapchainImages, swapchainImageFormat, swapchainExtent);
        }
    }

    private static VkSurfaceFormatKHR chooseSwapSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        return availableFormats.stream()
                .filter(availableFormat -> availableFormat.format() == VK_FORMAT_B8G8R8_UNORM)
                .filter(availableFormat -> availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
                .findAny()
                .orElse(availableFormats.get(0));
    }

    private static int chooseSwapPresentMode(IntBuffer availablePresentModes) {
        for (int i = 0; i < availablePresentModes.capacity(); i++)
            if (availablePresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR)
                return availablePresentModes.get(i);

        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private static VkExtent2D chooseSwapExtent(VkSurfaceCapabilitiesKHR capabilities, int windowWidth, int windowHeight) {
        if (capabilities.currentExtent().width() != 0xFFFFFFFF)
            return capabilities.currentExtent();

        VkExtent2D actualExtent = VkExtent2D.malloc().set(windowWidth, windowHeight);

        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();

        actualExtent.width(org.joml.Math.clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        actualExtent.height(org.joml.Math.clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

        return actualExtent;
    }
}
