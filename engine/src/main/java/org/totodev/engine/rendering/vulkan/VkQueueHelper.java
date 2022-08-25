package org.totodev.engine.rendering.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;

public final class VkQueueHelper {
    public static class QueueFamilyIndices {
        // We use Integer to use null as the empty value
        public Integer graphicsFamily;
        public Integer presentFamily;

        public boolean isComplete() {
            return graphicsFamily != null && presentFamily != null;
        }

        public int[] unique() {
            return IntStream.of(graphicsFamily, presentFamily).distinct().toArray();
        }
    }

    public static VkQueueHelper.QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device, long surface) {
        VkQueueHelper.QueueFamilyIndices indices = new VkQueueHelper.QueueFamilyIndices();

        try (MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            IntBuffer presentSupport = stack.ints(VK_FALSE);

            for (int i = 0; i < queueFamilies.capacity() || !indices.isComplete(); i++) {
                if ((queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                    indices.graphicsFamily = i;

                vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface, presentSupport);

                if (presentSupport.get(0) == VK_TRUE)
                    indices.presentFamily = i;
            }

            return indices;
        }
    }
}
