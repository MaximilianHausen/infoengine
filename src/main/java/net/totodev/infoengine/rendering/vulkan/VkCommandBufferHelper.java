package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.Engine;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkCommandBufferHelper {
    public static VkCommandBuffer beginSingleTimeCommands(VkDevice device, long commandPool) {
        try (MemoryStack stack = stackPush()) {
            VkCommandBuffer commandBuffer = createCommandBuffers(device, commandPool, 1).get(0);

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            vkBeginCommandBuffer(commandBuffer, beginInfo);

            return commandBuffer;
        }
    }

    public static void endSingleTimeCommands(VkDevice device, long commandPool, VkCommandBuffer commandBuffer, VkQueue queue) {
        try (MemoryStack stack = stackPush()) {
            vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo.Buffer submitInfo = VkSubmitInfo.calloc(1, stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(commandBuffer));

            vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
            vkQueueWaitIdle(queue);

            vkFreeCommandBuffers(device, commandPool, commandBuffer);
        }
    }

    public static long createCommandPool(int queueFamily) {
        return createCommandPool(Engine.getLogicalDevice(), queueFamily);
    }
    public static long createCommandPool(VkDevice device, int queueFamily) {
        try (MemoryStack stack = stackPush()) {
            VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            poolInfo.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
            poolInfo.queueFamilyIndex(queueFamily);

            LongBuffer pCommandPool = stack.mallocLong(1);
            if (vkCreateCommandPool(device, poolInfo, null, pCommandPool) != VK_SUCCESS)
                throw new RuntimeException("Failed to create command pool");

            return pCommandPool.get(0);
        }
    }

    public static MutableList<VkCommandBuffer> createCommandBuffers(long commandPool, int amount) {
        return createCommandBuffers(Engine.getLogicalDevice(), commandPool, amount);
    }
    public static MutableList<VkCommandBuffer> createCommandBuffers(VkDevice device, long commandPool, int amount) {
        try (MemoryStack stack = stackPush()) {
            MutableList<VkCommandBuffer> commandBuffers = Lists.mutable.empty();

            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.commandPool(commandPool);
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandBufferCount(amount);

            PointerBuffer pCommandBuffers = stack.mallocPointer(amount);
            if (vkAllocateCommandBuffers(device, allocInfo, pCommandBuffers) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate command buffers");

            for (int i = 0; i < amount; i++)
                commandBuffers.add(new VkCommandBuffer(pCommandBuffers.get(i), device));

            return commandBuffers;
        }
    }
}
