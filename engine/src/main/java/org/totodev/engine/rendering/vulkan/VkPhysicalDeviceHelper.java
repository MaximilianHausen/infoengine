package org.totodev.engine.rendering.vulkan;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.core.Engine;
import org.totodev.vulkan.QueueFamilies;

import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkPhysicalDeviceHelper {
    public static VkPhysicalDevice pickPhysicalDevice(long surface, IntIterable requiredQueueFamilies, Iterable<String> requiredExtensions) {
        return pickPhysicalDevice(Engine.getVkInstance(), surface, requiredQueueFamilies, requiredExtensions);
    }
    public static VkPhysicalDevice pickPhysicalDevice(VkInstance instance, long surface, IntIterable requiredQueueFamilies, Iterable<String> requiredExtensions) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, deviceCount, null);

            if (deviceCount.get(0) == 0)
                throw new RuntimeException("Failed to find GPUs with Vulkan support");

            PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, ppPhysicalDevices);

            for (int i = 0; i < ppPhysicalDevices.capacity(); i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(ppPhysicalDevices.get(i), instance);

                if (isDeviceSuitable(device, surface, requiredExtensions))
                    return device;
            }

            throw new RuntimeException("Failed to find a suitable GPU");
        }
    }

    private static boolean isDeviceSuitable(VkPhysicalDevice device, long surface, Iterable<String> requiredExtensions) {
        try (MemoryStack stack = stackPush()) {
            QueueFamilies indices = new QueueFamilies(device);
            boolean extensionsSupported = checkDeviceExtensionSupport(device, Sets.mutable.ofAll(requiredExtensions));
            boolean swapChainAdequate = false;

            VkPhysicalDeviceFeatures supportedFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            vkGetPhysicalDeviceFeatures(device, supportedFeatures);

            if (extensionsSupported) {
                VkSwapchainHelper.SwapchainSupportDetails swapChainSupport = VkSwapchainHelper.querySwapChainSupport(device, surface);
                swapChainAdequate = swapChainSupport.formats().hasRemaining() && swapChainSupport.presentModes().hasRemaining();
            }

            return /*indices.isComplete() &&*/ extensionsSupported && swapChainAdequate &&
                    supportedFeatures.samplerAnisotropy();
        }
    }

    // requiredExtensions will contain all extensions that are not supported
    private static boolean checkDeviceExtensionSupport(VkPhysicalDevice device, MutableSet<String> requiredExtensions) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer extensionCount = stack.ints(0);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, null);

            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.malloc(extensionCount.get(0), stack);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, availableExtensions);

            return requiredExtensions.isEmpty() || availableExtensions.stream()
                    .map(VkExtensionProperties::extensionNameString)
                    .anyMatch(s -> requiredExtensions.remove(s) && requiredExtensions.isEmpty());
        }
    }
}
