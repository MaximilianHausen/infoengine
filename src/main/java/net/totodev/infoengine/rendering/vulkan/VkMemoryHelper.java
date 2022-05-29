package net.totodev.infoengine.rendering.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkMemoryHelper {
    public static long allocateMemory(VkDevice device, VkPhysicalDevice physicalDevice, VkMemoryRequirements requirements, int properties) {
        try (MemoryStack stack = stackPush()) {
            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(requirements.size());
            allocInfo.memoryTypeIndex(VkMemoryHelper.findMemoryType(physicalDevice, requirements.memoryTypeBits(), properties));

            LongBuffer pMemory = stack.mallocLong(1);
            if (vkAllocateMemory(device, allocInfo, null, pMemory) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate image memory");

            return pMemory.get(0);
        }
    }

    public static int findMemoryType(VkPhysicalDevice physicalDevice, int typeFilter, int properties) {
        VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.malloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memProperties);

        for (int i = 0; i < memProperties.memoryTypeCount(); i++)
            if ((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties)
                return i;

        throw new RuntimeException("Failed to find suitable memory type");
    }
}
