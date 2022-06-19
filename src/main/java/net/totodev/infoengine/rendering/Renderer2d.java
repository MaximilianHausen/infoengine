package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.*;
import net.totodev.infoengine.core.components.Transform2d;
import net.totodev.infoengine.ecs.*;
import net.totodev.infoengine.rendering.vulkan.*;
import net.totodev.infoengine.util.*;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Renderer2d extends BaseSystem {
    @CachedComponent
    private VulkanObjects vulkanObjects;
    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private Camera2d camera;

    @Override
    public void start(Scene scene) {
        super.start(scene);

        if (!getScene().hasGlobalComponent(VulkanObjects.class))
            getScene().addGlobalComponent(new VulkanObjects());

        Window window = Engine.getMainWindow();

        /*vulkanObjects.descriptorSetLayout = VkDescriptorHelper.createDescriptorSetLayout(Engine.getLogicalDevice(),
                new VkDescriptorHelper.DescriptorBindingInfo(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 1, VK_SHADER_STAGE_FRAGMENT_BIT));*/

        vulkanObjects.renderPass = VkRenderPassHelper.createRenderPass(window.getVkImageFormat());
        VertexAttributeLayout vertexLayout = new VertexAttributeLayout(VK_VERTEX_INPUT_RATE_INSTANCE)
                .addAttribute(VK_FORMAT_R32G32_SFLOAT, 2 * Float.BYTES) // Size
                .addAttribute(VK_FORMAT_R16_SINT, Float.BYTES) // Texture Index
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES) // Model Matrix 1
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES) // Model Matrix 2
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES) // Model Matrix 3
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES); // Model Matrix 4
        vulkanObjects.graphicsPipeline = VkPipelineHelper.createGraphicsPipeline(Engine.getLogicalDevice(), window.getVkExtent(), vulkanObjects.renderPass, vertexLayout, vulkanObjects.descriptorSetLayout);

        vulkanObjects.commandPool = VkCommandBufferHelper.createCommandPool(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT, Engine.getGraphicsQueueFamily());

        // Per-Frame resources
        VkCommandBufferHelper.createCommandBuffers(vulkanObjects.commandPool, window.getVkImages().count(l -> true))
                .forEachWithIndex((c, j) -> vulkanObjects.frameData[j].commandBuffer = c);
        VkFramebufferHelper.createFramebuffers(window.getVkImageViews(), window.getVkExtent().width(), window.getVkExtent().height(), vulkanObjects.renderPass)
                .forEachWithIndex((f, j) -> vulkanObjects.frameData[j].framebuffer = f);

        VkSyncObjectHelper.createSemaphores(2).forEachWithIndex((s, j) -> vulkanObjects.frameData[j].imageAvailableSemaphore = s);
        VkSyncObjectHelper.createSemaphores(2).forEachWithIndex((s, j) -> vulkanObjects.frameData[j].renderFinishedSemaphore = s);
        VkSyncObjectHelper.createFences(2).forEachWithIndex((s, j) -> vulkanObjects.frameData[j].inFlightFence = s);

        VkImageHelper.VkImage image = VkImageHelper.createTextureImage(vulkanObjects.commandPool, IO.loadImageFromFile(IO.getFileFromResource("image.png")));
        long imageView = VkImageHelper.createImageView(image.image(), VK_FORMAT_R8G8B8A8_SRGB);
        long sampler = VkImageHelper.createTextureSampler(VK_FILTER_NEAREST, VK_FILTER_NEAREST, VK_SAMPLER_ADDRESS_MODE_REPEAT, 0);

        /*vulkanObjects.descriptorPool = VkDescriptorHelper.createDescriptorPool(Engine.getLogicalDevice(), 2,
                new VkDescriptorHelper.DescriptorPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 2));
        for (int i = 0; i < 2; i++) {
            vulkanObjects.frameData[i].descriptorSet = VkDescriptorHelper.createDescriptorSet(Engine.getLogicalDevice(), vulkanObjects.descriptorPool, vulkanObjects.descriptorSetLayout,
                    new VkDescriptorHelper.DescriptorImageBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 0,
                            new VkDescriptorHelper.Image(imageView, sampler, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)));
        }*/
    }

    private int lastImageIndex = 1;

    @EventSubscriber(CoreEvents.Update)
    public void drawFrame() {
        Engine.executeOnMainThread(GLFW::glfwPollEvents);

        try (MemoryStack stack = stackPush()) {
            VulkanObjects.FrameData frameData = vulkanObjects.frameData[lastImageIndex == 0 ? 1 : 0];

            LongBuffer inFlightFence = stack.longs(frameData.inFlightFence);
            vkWaitForFences(Engine.getLogicalDevice(), inFlightFence, true, Integer.MAX_VALUE);
            vkResetFences(Engine.getLogicalDevice(), inFlightFence);

            IntBuffer pImageIndex = stack.mallocInt(1);
            vkAcquireNextImageKHR(Engine.getLogicalDevice(), Engine.getMainWindow().getVkSwapchain(), 1000000000, frameData.imageAvailableSemaphore, VK_NULL_HANDLE, pImageIndex);
            int imageIndex = pImageIndex.get(0);

            LongBuffer waitSemaphores = stack.longs(frameData.imageAvailableSemaphore);
            IntBuffer waitStages = stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            LongBuffer signalSemaphores = stack.longs(frameData.renderFinishedSemaphore);

            // Build instance buffer
            var entities = getScene().getAllEntities();
            ByteBuffer instanceData = stack.malloc((2 * Float.BYTES + Integer.BYTES + 16 * Float.BYTES) * entities.count(e -> true));
            entities.forEach(e -> {
                Vector2f pos = transform.getPosition(e, new Vector2f());
                new InstanceData(new Vector2f(1, 1), 0, new Matrix4f().setTranslation(pos.x, pos.y, 0)).writeToBuffer(instanceData);
            });
            VkBufferHelper.VkBuffer instanceBuffer = VkBufferHelper.createFilledBuffer(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, instanceData, null);

            VkCommandBuffer commandBuffer = frameData.commandBuffer;
            vkResetCommandBuffer(commandBuffer, 0);
            recordCommandBuffer(commandBuffer, imageIndex, CameraMatrices.fromCamera(camera, transform, getScene().getEntitiesByComponents(Camera2d.class).getFirst()), instanceBuffer);

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
            presentInfo.pImageIndices(pImageIndex);

            vkQueuePresentKHR(Engine.getPresentQueue(), presentInfo);

            lastImageIndex = imageIndex;
        }
    }

    private void recordCommandBuffer(VkCommandBuffer commandBuffer, int imageIndex, IBufferWritable pushConstants, VkBufferHelper.VkBuffer instanceBuffer) {
        try (MemoryStack stack = stackPush()) {
            VulkanObjects.FrameData frameData = vulkanObjects.frameData[imageIndex];

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            if (vkBeginCommandBuffer(commandBuffer, beginInfo) != VK_SUCCESS)
                throw new RuntimeException("Failed to begin recording command buffer");

            VkRect2D renderArea = VkRect2D.calloc(stack);
            renderArea.offset(VkOffset2D.calloc(stack).set(0, 0));
            renderArea.extent(Engine.getMainWindow().getVkExtent());

            VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
            clearValues.color().float32(stack.floats(0.0f, 0.0f, 0.0f, 1.0f));

            VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.calloc(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO);
            renderPassInfo.renderPass(vulkanObjects.renderPass);
            renderPassInfo.framebuffer(frameData.framebuffer);
            renderPassInfo.renderArea(renderArea);
            renderPassInfo.pClearValues(clearValues);

            vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE);
            {
                vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, vulkanObjects.graphicsPipeline.pipeline());

                LongBuffer vertexBuffers = stack.longs(instanceBuffer.buffer());
                LongBuffer offsets = stack.longs(0);
                vkCmdBindVertexBuffers(commandBuffer, 0, vertexBuffers, offsets);

                //vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, vulkanObjects.graphicsPipeline.pipelineLayout(), 0, stack.longs(frameData.descriptorSet), null);

                ByteBuffer pushConstantBuffer = stack.malloc(pushConstants.bytes());
                pushConstants.writeToBuffer(pushConstantBuffer);
                vkCmdPushConstants(commandBuffer, vulkanObjects.graphicsPipeline.pipelineLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0, pushConstantBuffer);

                vkCmdDraw(commandBuffer, 6, 1, 0, 0);
            }
            vkCmdEndRenderPass(commandBuffer);

            if (vkEndCommandBuffer(commandBuffer) != VK_SUCCESS)
                throw new RuntimeException("Failed to record command buffer");
        }
    }
}
