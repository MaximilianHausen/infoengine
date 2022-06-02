package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.*;
import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public final class VkSwapchainHelper {
    public record SwapchainSupportDetails(VkSurfaceCapabilitiesKHR capabilities, VkSurfaceFormatKHR.Buffer formats, IntBuffer presentModes) {
    }
    public static SwapchainSupportDetails querySwapChainSupport(VkPhysicalDevice device, long surface) {
        MemoryStack stack = stackGet();

        VkSurfaceCapabilitiesKHR capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, capabilities);

        IntBuffer count = stack.ints(0);
        vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, null);

        VkSurfaceFormatKHR.Buffer formats = null;
        if (count.get(0) != 0) {
            formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, null);

        IntBuffer presentModes = null;
        if (count.get(0) != 0) {
            presentModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, presentModes);
        }

        return new SwapchainSupportDetails(capabilities, formats, presentModes);
    }

    public record SwapchainCreationResult(long swapchain, LongList images, int imageFormat, VkExtent2D extent) {
    }
    public static SwapchainCreationResult createSwapChain(Window window) {
        return createSwapChain(Engine.getLogicalDevice(), window.getVkSurface(), window.getSize().x, window.getSize().y);
    }
    public static SwapchainCreationResult createSwapChain(VkDevice device, long surface, int sizeX, int sizeY) {
        try (MemoryStack stack = stackPush()) {
            SwapchainSupportDetails swapChainSupport = querySwapChainSupport(device.getPhysicalDevice(), surface);

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(swapChainSupport.formats);
            int presentMode = chooseSwapPresentMode(swapChainSupport.presentModes);
            VkExtent2D extent = chooseSwapExtent(swapChainSupport.capabilities, sizeX, sizeY);

            IntBuffer imageCount = stack.ints(swapChainSupport.capabilities.minImageCount() + 1);

            if (swapChainSupport.capabilities.maxImageCount() > 0 && imageCount.get(0) > swapChainSupport.capabilities.maxImageCount())
                imageCount.put(0, swapChainSupport.capabilities.maxImageCount());

            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(surface);
            // Image settings
            createInfo.minImageCount(imageCount.get(0));
            createInfo.imageFormat(surfaceFormat.format());
            createInfo.imageColorSpace(surfaceFormat.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(1);
            createInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            VkQueueHelper.QueueFamilyIndices indices = VkQueueHelper.findQueueFamilies(device.getPhysicalDevice(), surface);

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
            if (vkCreateSwapchainKHR(device, createInfo, null, pSwapChain) != VK_SUCCESS)
                throw new RuntimeException("Failed to create swap chain");
            long swapchain = pSwapChain.get(0);

            vkGetSwapchainImagesKHR(device, swapchain, imageCount, null);
            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));
            vkGetSwapchainImagesKHR(device, swapchain, imageCount, pSwapchainImages);
            MutableLongList swapchainImages = LongLists.mutable.empty();

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
