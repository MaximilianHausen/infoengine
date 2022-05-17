package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.Engine;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkSyncObjectHelper {
    public static MutableLongList createSemaphores(int amount) {
        return createSemaphores(Engine.getLogicalDevice(), amount);
    }
    public static MutableLongList createSemaphores(VkDevice device, int amount) {
        try (MemoryStack stack = stackPush()) {
            MutableLongList semaphores = LongLists.mutable.empty();

            VkSemaphoreCreateInfo semaphoreInfo = VkSemaphoreCreateInfo.calloc(stack);
            semaphoreInfo.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

            LongBuffer semaphore = stack.mallocLong(1);

            for (int i = 0; i < amount; i++) {
                if (vkCreateSemaphore(device, semaphoreInfo, null, semaphore) != VK_SUCCESS)
                    throw new RuntimeException("Failed to create semaphores!");

                semaphores.add(semaphore.get(0));
            }

            return semaphores;
        }
    }

    public static MutableLongList createFences(int amount) {
        return createFences(Engine.getLogicalDevice(), amount);
    }
    public static MutableLongList createFences(VkDevice device, int amount) {
        try (MemoryStack stack = stackPush()) {
            MutableLongList fences = LongLists.mutable.empty();

            VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc(stack);
            fenceInfo.sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO);
            fenceInfo.flags(VK_FENCE_CREATE_SIGNALED_BIT);

            LongBuffer fence = stack.mallocLong(1);

            for (int i = 0; i < amount; i++) {
                if (vkCreateFence(device, fenceInfo, null, fence) != VK_SUCCESS)
                    throw new RuntimeException("Failed to create fences!");

                fences.add(fence.get(0));
            }

            return fences;
        }
    }
}
