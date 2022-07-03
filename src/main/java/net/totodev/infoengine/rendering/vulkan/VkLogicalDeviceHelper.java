package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.util.BufferUtils;
import org.eclipse.collections.api.RichIterable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.function.Consumer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkLogicalDeviceHelper {
    public record LogicalDeviceCreationResult(VkDevice device, VkQueue graphicsQueue, VkQueue presentQueue) {
    }

    public static LogicalDeviceCreationResult createLogicalDevice(VkPhysicalDevice physicalDevice, long surface, RichIterable<String> extensions, Consumer<VkPhysicalDeviceFeatures> deviceFeatureConfig, long pNext) {
        try (MemoryStack stack = stackPush()) {
            VkQueueHelper.QueueFamilyIndices indices = VkQueueHelper.findQueueFamilies(physicalDevice, surface);
            int[] uniqueQueueFamilies = indices.unique();

            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(uniqueQueueFamilies.length, stack);

            for (int i = 0; i < uniqueQueueFamilies.length; i++) {
                VkDeviceQueueCreateInfo queueCreateInfo = queueCreateInfos.get(i);
                queueCreateInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
                queueCreateInfo.queueFamilyIndex(uniqueQueueFamilies[i]);
                queueCreateInfo.pQueuePriorities(stack.floats(1.0f));
            }

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            deviceFeatureConfig.accept(deviceFeatures);
            deviceFeatures.samplerAnisotropy(true);

            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(queueCreateInfos)
                    .pEnabledFeatures(deviceFeatures)
                    .ppEnabledExtensionNames(BufferUtils.asPointerBuffer(extensions))
                    .pNext(pNext);

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);
            if (vkCreateDevice(physicalDevice, createInfo, null, pDevice) != VK_SUCCESS)
                throw new RuntimeException("Failed to create logical device");
            VkDevice device = new VkDevice(pDevice.get(0), physicalDevice, createInfo);

            PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);
            vkGetDeviceQueue(device, indices.graphicsFamily, 0, pQueue);
            VkQueue graphicsQueue = new VkQueue(pQueue.get(0), device);
            vkGetDeviceQueue(device, indices.presentFamily, 0, pQueue);
            VkQueue presentQueue = new VkQueue(pQueue.get(0), device);

            return new LogicalDeviceCreationResult(device, graphicsQueue, presentQueue);
        }
    }
}
