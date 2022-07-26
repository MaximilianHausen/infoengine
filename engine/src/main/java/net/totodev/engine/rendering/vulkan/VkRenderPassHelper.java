package net.totodev.engine.rendering.vulkan;

import net.totodev.engine.core.Engine;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.function.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.*;

public final class VkRenderPassHelper {
    public static long createRenderPass(int imageFormat) {
        return createRenderPass(Engine.getLogicalDevice(), imageFormat, 1, null, 1, null);
    }
    public static long createRenderPass(VkDevice device, int imageFormat, int attachmentCount, @Nullable BiConsumer<VkAttachmentDescription.Buffer, VkAttachmentReference.Buffer> attachmentConfig, int subpassCount, @Nullable Consumer<VkSubpassDescription.Buffer> subpassConfig) {
        try (MemoryStack stack = stackPush()) {
            VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.calloc(attachmentCount, stack);
            attachments.format(imageFormat);
            attachments.samples(VK_SAMPLE_COUNT_1_BIT);
            attachments.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            attachments.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            attachments.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            attachments.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            attachments.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            attachments.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            VkAttachmentReference.Buffer attachmentRef = VkAttachmentReference.calloc(attachmentCount, stack);
            attachmentRef.attachment(0);
            attachmentRef.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            if (attachmentConfig != null) attachmentConfig.accept(attachments, attachmentRef);

            //TODO: Non-Color attachments
            VkSubpassDescription.Buffer subpasses = VkSubpassDescription.calloc(subpassCount, stack);
            subpasses.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            subpasses.colorAttachmentCount(attachmentCount);
            subpasses.pColorAttachments(attachmentRef);

            if (subpassConfig != null) subpassConfig.accept(subpasses);

            //TODO: Subpass dependency config
            VkSubpassDependency.Buffer dependency = VkSubpassDependency.calloc(1, stack);
            dependency.srcSubpass(VK_SUBPASS_EXTERNAL);
            dependency.dstSubpass(0);
            dependency.srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            dependency.srcAccessMask(0);
            dependency.dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            dependency.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

            VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO);
            renderPassInfo.pAttachments(attachments);
            renderPassInfo.pSubpasses(subpasses);
            renderPassInfo.pDependencies(dependency);

            LongBuffer pRenderPass = stack.mallocLong(1);
            if (vkCreateRenderPass(device, renderPassInfo, null, pRenderPass) != VK_SUCCESS)
                throw new RuntimeException("Failed to create render pass");

            return pRenderPass.get(0);
        }
    }
}
