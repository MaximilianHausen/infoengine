package org.totodev.engine.rendering.vulkan;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.core.Engine;

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

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pCommandBuffers(stack.pointers(commandBuffer));

            LongBuffer pFence = stack.callocLong(1);
            vkCreateFence(device, VkFenceCreateInfo.calloc(stack).sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO), null, pFence);

            synchronized (queue) {
                vkQueueSubmit(queue, submitInfo, pFence.get(0));
            }

            vkWaitForFences(device, pFence, true, Long.MAX_VALUE);
            vkDestroyFence(device, pFence.get(0), null);
            vkFreeCommandBuffers(device, commandPool, commandBuffer);
        }
    }

    public static long createCommandPool(int flags, int queueFamily) {
        return createCommandPool(Engine.getLogicalDevice(), flags, queueFamily);
    }
    public static long createCommandPool(VkDevice device, int flags, int queueFamily) {
        try (MemoryStack stack = stackPush()) {
            VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            poolInfo.flags(flags);
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
