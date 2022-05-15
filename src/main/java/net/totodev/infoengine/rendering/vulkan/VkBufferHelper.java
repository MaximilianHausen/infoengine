package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.Engine;
import net.totodev.infoengine.core.annotations.Out;
import org.eclipse.collections.api.list.primitive.IntList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class VkBufferHelper {
    public static void createVertexBuffer(long commandPool, ByteBuffer vertexData, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        createVertexBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), Engine.getGraphicsQueue(), commandPool, vertexData, sharedQueues, pBuffer, pBufferMemory);
    }
    public static void createVertexBuffer(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, ByteBuffer vertexData, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        createFilledBuffer(device, physicalDevice, transferQueue, commandPool, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, vertexData, sharedQueues, pBuffer, pBufferMemory);
    }

    public static void createIndexBuffer(long commandPool, ByteBuffer vertexData, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        createIndexBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), Engine.getGraphicsQueue(), commandPool, vertexData, sharedQueues, pBuffer, pBufferMemory);
    }
    public static void createIndexBuffer(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, ByteBuffer indices, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        createFilledBuffer(device, physicalDevice, transferQueue, commandPool, VK_BUFFER_USAGE_INDEX_BUFFER_BIT, indices, sharedQueues, pBuffer, pBufferMemory);
    }

    public static void createFilledBuffer(long commandPool, int bufferUsage, ByteBuffer bufferData, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        createFilledBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), Engine.getGraphicsQueue(), commandPool, bufferUsage, bufferData, sharedQueues, pBuffer, pBufferMemory);
    }
    public static void createFilledBuffer(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, int bufferUsage, ByteBuffer bufferData, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        try (MemoryStack stack = stackPush()) {
            long bufferSize = bufferData.capacity();

            VkBufferHelper.createBuffer(device, physicalDevice, bufferSize,
                    VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    sharedQueues,
                    pBuffer,
                    pBufferMemory);

            long stagingBuffer = pBuffer.get(0);
            long stagingBufferMemory = pBufferMemory.get(0);

            PointerBuffer data = stack.mallocPointer(1);

            vkMapMemory(device, stagingBufferMemory, 0, bufferSize, 0, data);
            {
                data.getByteBuffer(0, (int) bufferSize).put(bufferData);
            }
            vkUnmapMemory(device, stagingBufferMemory);

            VkBufferHelper.createBuffer(device, physicalDevice, bufferSize,
                    VK_BUFFER_USAGE_TRANSFER_DST_BIT | bufferUsage,
                    VK_MEMORY_HEAP_DEVICE_LOCAL_BIT,
                    sharedQueues,
                    pBuffer,
                    pBufferMemory);

            VkBufferHelper.copyBuffer(device, transferQueue, commandPool, stagingBuffer, pBuffer.get(0), bufferSize);

            vkDestroyBuffer(device, stagingBuffer, null);
            vkFreeMemory(device, stagingBufferMemory, null);
        }
    }

    public static void createBuffer(VkDevice device, VkPhysicalDevice physicalDevice, long size, int usage, int properties, @Nullable IntList sharedQueues, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        try (MemoryStack stack = stackPush()) {
            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.calloc(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            bufferInfo.sharingMode(sharedQueues == null ? VK_SHARING_MODE_EXCLUSIVE : VK_SHARING_MODE_CONCURRENT);
            if (sharedQueues != null) bufferInfo.pQueueFamilyIndices(stack.ints(sharedQueues.toArray()));

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

    private static int findMemoryType(VkPhysicalDevice physicalDevice, int typeFilter, int properties) {
        VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.malloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memProperties);

        for (int i = 0; i < memProperties.memoryTypeCount(); i++)
            if ((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties)
                return i;

        throw new RuntimeException("Failed to find suitable memory type");
    }

    public static void copyBuffer(long commandPool, long srcBuffer, long dstBuffer, long size) {
        copyBuffer(Engine.getLogicalDevice(), Engine.getGraphicsQueue(), commandPool, srcBuffer, dstBuffer, size);
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
