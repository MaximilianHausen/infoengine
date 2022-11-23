package org.totodev.engine.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public record QueueFamily(VkPhysicalDevice device, int familyIndex, int flags, int queueCount) {
    public VkQueue getQueue(VkDevice device, int queueIndex) {
        try (MemoryStack stack = stackPush()) {
            PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);
            vkGetDeviceQueue(device, familyIndex, queueIndex, pQueue);
            return new VkQueue(pQueue.get(0), device);
        }
    }
}
