package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.Engine;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkBufferHelper {
    public record VkBuffer(long buffer, long bufferMemory) {
    }

    public static VkBuffer createBuffer(VkDevice device, VkPhysicalDevice physicalDevice, long size, int usage, int properties, @Nullable IntList sharedQueues) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer pBuffer = stack.mallocLong(1), pBufferMemory = stack.mallocLong(1);

            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.calloc(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            bufferInfo.sharingMode(sharedQueues == null ? VK_SHARING_MODE_EXCLUSIVE : VK_SHARING_MODE_CONCURRENT);
            if (sharedQueues != null) bufferInfo.pQueueFamilyIndices(stack.ints(sharedQueues.toArray()));

            if (vkCreateBuffer(device, bufferInfo, null, pBuffer) != VK_SUCCESS)
                throw new RuntimeException("Failed to create buffer");

            VkMemoryRequirements memRequirements = VkMemoryRequirements.malloc(stack);
            vkGetBufferMemoryRequirements(device, pBuffer.get(0), memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(findMemoryType(physicalDevice, memRequirements.memoryTypeBits(), properties));

            if (vkAllocateMemory(device, allocInfo, null, pBufferMemory) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate buffer memory");

            vkBindBufferMemory(device, pBuffer.get(0), pBufferMemory.get(0), 0);

            return new VkBuffer(pBuffer.get(0), pBufferMemory.get(0));
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

    //region Specific
    public static VkBuffer createVertexBuffer(long commandPool, ByteBuffer vertexData, @Nullable IntList sharedQueues) {
        return createVertexBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), Engine.getGraphicsQueue(), commandPool, vertexData, sharedQueues);
    }
    public static VkBuffer createVertexBuffer(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, ByteBuffer vertexData, @Nullable IntList sharedQueues) {
        return createFilledBufferStaged(device, physicalDevice, transferQueue, commandPool, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, vertexData, sharedQueues);
    }

    public static VkBuffer createIndexBuffer(long commandPool, ByteBuffer vertexData, @Nullable IntList sharedQueues) {
        return createIndexBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), Engine.getGraphicsQueue(), commandPool, vertexData, sharedQueues);
    }
    public static VkBuffer createIndexBuffer(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, ByteBuffer indices, @Nullable IntList sharedQueues) {
        return createFilledBufferStaged(device, physicalDevice, transferQueue, commandPool, VK_BUFFER_USAGE_INDEX_BUFFER_BIT, indices, sharedQueues);
    }

    public static VkBuffer createUniformBuffer(int size) {
        return createUniformBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), size);
    }
    public static VkBuffer createUniformBuffer(VkDevice device, VkPhysicalDevice physicalDevice, int size) {
        return createBuffer(device, physicalDevice, size,
                VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                null);
    }

    public static MutableList<VkBuffer> createUniformBuffers(int size, int amount) {
        return createUniformBuffers(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), size, amount);
    }
    public static MutableList<VkBuffer> createUniformBuffers(VkDevice device, VkPhysicalDevice physicalDevice, int size, int amount) {
        MutableList<VkBuffer> buffers = Lists.mutable.empty();

        for (int i = 0; i < amount; i++) {
            buffers.add(createBuffer(device, physicalDevice, size,
                    VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    null));
        }

        return buffers;
    }
    //endregion

    public static VkBuffer createFilledBuffer(int bufferUsage, ByteBuffer bufferData, @Nullable IntList sharedQueues) {
        return createFilledBuffer(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), bufferUsage, bufferData, sharedQueues);
    }
    public static VkBuffer createFilledBuffer(VkDevice device, VkPhysicalDevice physicalDevice, int bufferUsage, ByteBuffer bufferData, @Nullable IntList sharedQueues) {
        VkBuffer buffer = createBuffer(device, physicalDevice, bufferData.capacity(),
                bufferUsage,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                sharedQueues);

        fillBuffer(device, buffer.bufferMemory, 0, bufferData);

        return buffer;
    }

    public static VkBuffer createFilledBufferStaged(long commandPool, int bufferUsage, ByteBuffer bufferData, @Nullable IntList sharedQueues) {
        return createFilledBufferStaged(Engine.getLogicalDevice(), Engine.getPhysicalDevice(), Engine.getGraphicsQueue(), commandPool, bufferUsage, bufferData, sharedQueues);
    }
    public static VkBuffer createFilledBufferStaged(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, int bufferUsage, ByteBuffer bufferData, @Nullable IntList sharedQueues) {
        VkBuffer stagingBuffer = createFilledBuffer(device, physicalDevice, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, bufferData, sharedQueues);

        VkBuffer finalBuffer = createBuffer(device, physicalDevice, bufferData.capacity(),
                VK_BUFFER_USAGE_TRANSFER_DST_BIT | bufferUsage,
                VK_MEMORY_HEAP_DEVICE_LOCAL_BIT,
                sharedQueues);

        copyBuffer(device, transferQueue, commandPool, stagingBuffer.buffer, finalBuffer.buffer, new BufferCopyRegion(0, 0, bufferData.capacity()));

        vkDestroyBuffer(device, stagingBuffer.buffer, null);
        vkFreeMemory(device, stagingBuffer.bufferMemory, null);

        return finalBuffer;
    }

    public record BufferCopyRegion(long srcOffset, long dstOffset, long size) {
    }
    public static void copyBuffer(long commandPool, long srcBuffer, long dstBuffer, BufferCopyRegion... regions) {
        copyBuffer(Engine.getLogicalDevice(), Engine.getGraphicsQueue(), commandPool, srcBuffer, dstBuffer, regions);
    }
    public static void copyBuffer(VkDevice device, VkQueue transferQueue, long commandPool, long srcBuffer, long dstBuffer, BufferCopyRegion... regions) {
        try (MemoryStack stack = stackPush()) {
            VkCommandBuffer commandBuffer = VkCommandBufferHelper.createCommandBuffers(device, commandPool, 1).get(0);
            PointerBuffer pCommandBuffer = stack.pointers(commandBuffer.address());

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            vkBeginCommandBuffer(commandBuffer, beginInfo);
            {
                VkBufferCopy.Buffer copyRegions = VkBufferCopy.calloc(regions.length, stack);
                for (int i = 0; i < regions.length; i++) {
                    BufferCopyRegion region = regions[i];
                    VkBufferCopy copyRegion = copyRegions.get(i);
                    copyRegion.srcOffset(region.srcOffset);
                    copyRegion.dstOffset(region.dstOffset);
                    copyRegion.size(region.size);
                }

                vkCmdCopyBuffer(commandBuffer, srcBuffer, dstBuffer, copyRegions);
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

    public static void fillBuffer(long bufferMemory, long offset, ByteBuffer data) {
        fillBuffer(Engine.getLogicalDevice(), bufferMemory, offset, data);
    }
    public static void fillBuffer(VkDevice device, long bufferMemory, long offset, ByteBuffer data) {
        try (MemoryStack stack = stackPush()) {
            PointerBuffer mappedRegion = stack.mallocPointer(1);
            vkMapMemory(device, bufferMemory, offset, data.capacity(), 0, mappedRegion);
            {
                mappedRegion.getByteBuffer(0, data.capacity()).put(data);
            }
            vkUnmapMemory(device, bufferMemory);
        }
    }
}
