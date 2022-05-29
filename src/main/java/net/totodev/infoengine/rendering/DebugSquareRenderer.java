package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.*;
import net.totodev.infoengine.ecs.*;
import net.totodev.infoengine.rendering.vulkan.*;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class DebugSquareRenderer extends BaseSystem {
    private VkBufferHelper.VkBuffer vertexBuffer;
    private VkBufferHelper.VkBuffer indexBuffer;
    private final MutableList<VkBufferHelper.VkBuffer> uniformBuffers = Lists.mutable.empty();

    private int currentFrame = 0;

    public void added(Scene scene) {
        super.added(scene);
        RendererConfig rendererConfig = scene.getGlobalComponent(RendererConfig.class);
        if (rendererConfig == null) scene.addGlobalComponent(new RendererConfig());
        rendererConfig = scene.getGlobalComponent(RendererConfig.class);

        Window window = Engine.getMainWindow();

        rendererConfig.descriptorSetLayout = VkDescriptorHelper.createDescriptorSetLayout(Engine.getLogicalDevice(),
                new VkDescriptorHelper.DescriptorBindingInfo(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1, VK_SHADER_STAGE_VERTEX_BIT));

        rendererConfig.renderPass = VkRenderPassHelper.createRenderPass(window.getVkImageFormat());
        rendererConfig.graphicsPipeline = VkPipelineHelper.createGraphicsPipeline(Engine.getLogicalDevice(), window.getVkExtent(), rendererConfig.renderPass, rendererConfig.descriptorSetLayout);
        rendererConfig.framebuffers.addAll(VkFramebufferHelper.createFramebuffers(window.getVkImageViews(), window.getVkExtent().width(), window.getVkExtent().height(), rendererConfig.renderPass));

        rendererConfig.commandPool = VkCommandBufferHelper.createCommandPool(Engine.getGraphicsQueueFamily());
        rendererConfig.commandBuffers.addAll(VkCommandBufferHelper.createCommandBuffers(rendererConfig.commandPool, window.getVkImages().count(l -> true)));

        rendererConfig.imageAvailableSemaphores.addAll(VkSyncObjectHelper.createSemaphores(2));
        rendererConfig.renderFinishedSemaphores.addAll(VkSyncObjectHelper.createSemaphores(2));
        rendererConfig.inFlightFences.addAll(VkSyncObjectHelper.createFences(2));

        try (MemoryStack stack = stackPush()) {
            ByteBuffer vertexData = stack.malloc(8 * 4);
            vertexData.asFloatBuffer().put(new float[]{-0.1f, -0.1f, 0.1f, -0.1f, 0.1f, 0.1f, -0.1f, 0.1f});
            ByteBuffer indexData = stack.malloc(6 * 2);
            indexData.asShortBuffer().put(new short[]{0, 1, 2, 2, 3, 0});

            vertexBuffer = VkBufferHelper.createVertexBuffer(rendererConfig.commandPool, vertexData, null);
            indexBuffer = VkBufferHelper.createIndexBuffer(rendererConfig.commandPool, indexData, null);

            uniformBuffers.addAllIterable(VkBufferHelper.createUniformBuffers(3 * 16 * Float.BYTES, 2).select(b -> {
                MatrixUbo data = new MatrixUbo();
                VkBufferHelper.writeBuffer(b.bufferMemory(), 0, data);
                return true;
            }));
        }

        rendererConfig.descriptorPool = VkDescriptorHelper.createDescriptorPool(Engine.getLogicalDevice(), 2,
                new VkDescriptorHelper.DescriptorPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 2));
        for (int i = 0; i < 2; i++) {
            rendererConfig.descriptorSets.add(VkDescriptorHelper.createDescriptorSet(Engine.getLogicalDevice(), rendererConfig.descriptorPool, rendererConfig.descriptorSetLayout,
                    new VkDescriptorHelper.DescriptorBufferBinding(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 0,
                            new VkDescriptorHelper.BufferRegion(uniformBuffers.get(i).buffer(), 0, 3 * 16 * Float.BYTES))));
        }
    }

    @EventSubscriber(CoreEvents.Update)
    private void drawFrame() {
        Engine.executeOnMainThread(GLFW::glfwPollEvents);

        try (MemoryStack stack = stackPush()) {
            RendererConfig rendererConfig = getScene().getGlobalComponent(RendererConfig.class);

            LongBuffer inFlightFence = stack.longs(rendererConfig.inFlightFences.get(currentFrame));
            vkWaitForFences(Engine.getLogicalDevice(), inFlightFence, true, Integer.MAX_VALUE);
            vkResetFences(Engine.getLogicalDevice(), inFlightFence);

            IntBuffer imageIndex = stack.mallocInt(1);
            vkAcquireNextImageKHR(Engine.getLogicalDevice(), Engine.getMainWindow().getVkSwapchain(), Integer.MAX_VALUE, rendererConfig.imageAvailableSemaphores.get(currentFrame), VK_NULL_HANDLE, imageIndex);

            LongBuffer waitSemaphores = stack.longs(rendererConfig.imageAvailableSemaphores.get(currentFrame));
            IntBuffer waitStages = stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            LongBuffer signalSemaphores = stack.longs(rendererConfig.renderFinishedSemaphores.get(currentFrame));

            VkCommandBuffer commandBuffer = rendererConfig.commandBuffers.get(currentFrame);
            vkResetCommandBuffer(commandBuffer, 0);
            recordCommandBuffer(commandBuffer, imageIndex.get(0), getScene());

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(commandBuffer));
            submitInfo.waitSemaphoreCount(1);
            submitInfo.pWaitSemaphores(waitSemaphores);
            submitInfo.pWaitDstStageMask(waitStages);
            submitInfo.pSignalSemaphores(signalSemaphores);

            if (vkQueueSubmit(Engine.getGraphicsQueue(), submitInfo, inFlightFence.get(0)) != VK_SUCCESS)
                throw new RuntimeException("Failed to submit draw command buffer!");

            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack);
            presentInfo.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);
            presentInfo.pWaitSemaphores(signalSemaphores);
            LongBuffer swapChains = stack.longs(Engine.getMainWindow().getVkSwapchain());
            presentInfo.swapchainCount(1);
            presentInfo.pSwapchains(swapChains);
            presentInfo.pImageIndices(imageIndex);

            vkQueuePresentKHR(Engine.getPresentQueue(), presentInfo);

            currentFrame = (currentFrame + 1) % 2;
        }
    }

    private void recordCommandBuffer(VkCommandBuffer commandBuffer, int imageIndex, Scene scene) {
        try (MemoryStack stack = stackPush()) {
            RendererConfig rendererConfig = scene.getGlobalComponent(RendererConfig.class);

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            if (vkBeginCommandBuffer(commandBuffer, beginInfo) != VK_SUCCESS)
                throw new RuntimeException("Failed to begin recording command buffer");
            {
                VkRect2D renderArea = VkRect2D.calloc(stack);
                renderArea.offset(VkOffset2D.calloc(stack).set(0, 0));
                renderArea.extent(Engine.getMainWindow().getVkExtent());

                VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
                clearValues.color().float32(stack.floats(0.0f, 0.0f, 0.0f, 1.0f));

                VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.calloc(stack);
                renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO);
                renderPassInfo.renderPass(rendererConfig.renderPass);
                renderPassInfo.framebuffer(rendererConfig.framebuffers.get(imageIndex));
                renderPassInfo.renderArea(renderArea);
                renderPassInfo.pClearValues(clearValues);

                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE);
                {
                    vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, rendererConfig.graphicsPipeline.pipeline());

                    LongBuffer vertexBuffers = stack.longs(vertexBuffer.buffer());
                    LongBuffer offsets = stack.longs(0);
                    vkCmdBindVertexBuffers(commandBuffer, 0, vertexBuffers, offsets);
                    vkCmdBindIndexBuffer(commandBuffer, indexBuffer.buffer(), 0, VK_INDEX_TYPE_UINT16);

                    vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, rendererConfig.graphicsPipeline.pipelineLayout(), 0, stack.longs(rendererConfig.descriptorSets.get(imageIndex)), null);

                    vkCmdDrawIndexed(commandBuffer, 6, 1, 0, 0, 0);
                }
                vkCmdEndRenderPass(commandBuffer);
            }
            if (vkEndCommandBuffer(commandBuffer) != VK_SUCCESS)
                throw new RuntimeException("Failed to record command buffer");
        }
    }
}
