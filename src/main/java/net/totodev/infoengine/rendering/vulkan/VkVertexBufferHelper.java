package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.annotations.Out;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class VkVertexBufferHelper {
    private void createVertexBuffer(VkDevice device, VkPhysicalDevice physicalDevice, VkQueue transferQueue, long commandPool, ByteBuffer vertexData, @Out LongBuffer pBuffer, @Out LongBuffer pBufferMemory) {
        try (MemoryStack stack = stackPush()) {
            long bufferSize = vertexData.capacity();

            VkBufferHelper.createBuffer(device, physicalDevice, bufferSize,
                    VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    VK_SHARING_MODE_EXCLUSIVE,
                    pBuffer,
                    pBufferMemory);

            long stagingBuffer = pBuffer.get(0);
            long stagingBufferMemory = pBufferMemory.get(0);

            PointerBuffer data = stack.mallocPointer(1);

            vkMapMemory(device, stagingBufferMemory, 0, bufferSize, 0, data);
            {
                data.getByteBuffer(0, (int) bufferSize).put(vertexData);
            }
            vkUnmapMemory(device, stagingBufferMemory);

            VkBufferHelper.createBuffer(device, physicalDevice, bufferSize,
                    VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
                    VK_MEMORY_HEAP_DEVICE_LOCAL_BIT,
                    VK_SHARING_MODE_EXCLUSIVE,
                    pBuffer,
                    pBufferMemory);

            VkBufferHelper.copyBuffer(device, transferQueue, commandPool, stagingBuffer, pBuffer.get(0), bufferSize);

            vkDestroyBuffer(device, stagingBuffer, null);
            vkFreeMemory(device, stagingBufferMemory, null);
        }
    }
}
