package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.annotations.Out;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class VkBufferHelper {
    public static void createBuffer(VkDevice device, VkPhysicalDevice physicalDevice, long size, int usage, int properties, int sharingMode, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        try (MemoryStack stack = stackPush()) {
            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.calloc(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            bufferInfo.sharingMode(sharingMode);

            if (vkCreateBuffer(device, bufferInfo, null, pBuffer) != VK_SUCCESS)
                throw new RuntimeException("Failed to create vertex buffer");

            VkMemoryRequirements memRequirements = VkMemoryRequirements.malloc(stack);
            vkGetBufferMemoryRequirements(device, pBuffer.get(0), memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(findMemoryType(physicalDevice, memRequirements.memoryTypeBits(), properties));

            if (vkAllocateMemory(device, allocInfo, null, pBufferMemory) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate vertex buffer memory");

            vkBindBufferMemory(device, pBuffer.get(0), pBufferMemory.get(0), 0);
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

    public static void copyBuffer(VkDevice device, VkQueue transferQueue, long commandPool, long srcBuffer, long dstBuffer, long size) {
        try (MemoryStack stack = stackPush()) {
            VkCommandBuffer commandBuffer = VkCommandBufferHelper.createCommandBuffers(device, commandPool, 1).get(0);
            PointerBuffer pCommandBuffer = stack.pointers(commandBuffer.address());

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            vkBeginCommandBuffer(commandBuffer, beginInfo);
            {
                VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1, stack);
                copyRegion.size(size);
                vkCmdCopyBuffer(commandBuffer, srcBuffer, dstBuffer, copyRegion);
            }
            vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(pCommandBuffer);

            if (vkQueueSubmit(transferQueue, submitInfo, VK_NULL_HANDLE) != VK_SUCCESS)
                throw new RuntimeException("Failed to submit copy command buffer");

            vkQueueWaitIdle(transferQueue);

            vkFreeCommandBuffers(device, commandPool, pCommandBuffer);
        }
    }
}
