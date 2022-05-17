package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.core.Engine;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkFramebufferHelper {
    public static MutableLongList createFramebuffers(LongIterable imageViews, int width, int height, long renderPass) {
        return createFramebuffers(Engine.getLogicalDevice(), imageViews, width, height, renderPass);
    }
    public static MutableLongList createFramebuffers(VkDevice device, LongIterable imageViews, int width, int height, long renderPass) {
        MutableLongList framebuffers = LongLists.mutable.empty();

        try (MemoryStack stack = stackPush()) {
            LongBuffer attachments = stack.mallocLong(1);
            LongBuffer pFramebuffer = stack.mallocLong(1);

            // Lets allocate the create info struct once and just update the pAttachments field each iteration
            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc(stack);
            framebufferInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO);
            framebufferInfo.renderPass(renderPass);
            framebufferInfo.width(width);
            framebufferInfo.height(height);
            framebufferInfo.layers(1);

            imageViews.forEach(imageView -> {
                attachments.put(0, imageView);

                framebufferInfo.pAttachments(attachments);

                if (vkCreateFramebuffer(device, framebufferInfo, null, pFramebuffer) != VK_SUCCESS)
                    throw new RuntimeException("Failed to create framebuffer");

                framebuffers.add(pFramebuffer.get(0));
            });

            return framebuffers;
        }
    }

    public static long createFramebuffer(long imageView, int width, int height, long renderPass) {
        return createFramebuffer(Engine.getLogicalDevice(), imageView, width, height, renderPass);
    }
    public static long createFramebuffer(VkDevice device, long imageView, int width, int height, long renderPass) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer attachments = stack.mallocLong(1);
            LongBuffer pFramebuffer = stack.mallocLong(1);

            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc(stack);
            framebufferInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO);
            framebufferInfo.renderPass(renderPass);
            framebufferInfo.width(width);
            framebufferInfo.height(height);
            framebufferInfo.layers(1);

            attachments.put(0, imageView);
            framebufferInfo.pAttachments(attachments);

            if (vkCreateFramebuffer(device, framebufferInfo, null, pFramebuffer) != VK_SUCCESS)
                throw new RuntimeException("Failed to create framebuffer");

            return pFramebuffer.get(0);
        }
    }
}
