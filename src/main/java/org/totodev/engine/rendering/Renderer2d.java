package org.totodev.engine.rendering;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.core.*;
import org.totodev.engine.core.components.Transform2d;
import org.totodev.engine.core.systems.Updater;
import org.totodev.engine.ecs.*;
import org.totodev.engine.rendering.vulkan.*;
import org.totodev.engine.resources.image.ImageProvider;
import org.totodev.engine.util.BufferWritable;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Renderer2d extends BaseSystem {
    public record FrameData(VkBufferHelper.VkBuffer instanceBuffer, long descriptorSet, int instanceCount) {
    }

    @CachedComponent
    private VulkanObjects vulkanObjects;
    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private Camera2d camera;
    @CachedComponent
    private Sprite2d sprite2d;
    @CachedComponent
    private PixelScale pixelScale;

    @Override
    public void start(Scene scene) {
        super.start(scene);

        if (!getScene().hasGlobalComponent(VulkanObjects.class))
            getScene().addGlobalComponent(new VulkanObjects());

        Window window = Engine.getMainWindow();

        vulkanObjects.descriptorSetLayout = VkDescriptorHelper.createDescriptorSetLayout(Engine.getLogicalDevice(),
                new VkDescriptorHelper.DescriptorBindingInfo(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 128, VK_SHADER_STAGE_FRAGMENT_BIT));

        vulkanObjects.renderPass = VkRenderPassHelper.createRenderPass(window.getVkImageFormat());
        VertexAttributeLayout vertexLayout = new VertexAttributeLayout(VK_VERTEX_INPUT_RATE_INSTANCE)
                .addAttribute(VK_FORMAT_R32_SINT, Integer.BYTES) // TexIndex
                .addAttribute(VK_FORMAT_R32G32_SFLOAT, 2 * Float.BYTES) // Size
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES) // Model Matrix 1
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES) // Model Matrix 2
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES) // Model Matrix 3
                .addAttribute(VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES); // Model Matrix 4

        vulkanObjects.graphicsPipeline = VkPipelineHelper.createGraphicsPipeline(Engine.getLogicalDevice(), window.getVkExtent(), vulkanObjects.renderPass, vertexLayout, vulkanObjects.descriptorSetLayout);

        vulkanObjects.commandPool = VkCommandBufferHelper.createCommandPool(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT, Engine.getGraphicsQueueFamily().familyIndex());

        // Per-Frame resources
        VkCommandBufferHelper.createCommandBuffers(vulkanObjects.commandPool, window.getVkImages().size())
                .forEachWithIndex((c, j) -> vulkanObjects.frameResources[j].commandBuffer = c);
        VkFramebufferHelper.createFramebuffers(window.getVkImageViews(), window.getVkExtent().width(), window.getVkExtent().height(), vulkanObjects.renderPass)
                .forEachWithIndex((f, j) -> vulkanObjects.frameResources[j].framebuffer = f);

        int imageCount = Engine.getMainWindow().getVkImages().size();

        VkSyncObjectHelper.createSemaphores(imageCount).forEachWithIndex((s, j) -> vulkanObjects.frameResources[j].imageAvailableSemaphore = s);
        VkSyncObjectHelper.createSemaphores(imageCount).forEachWithIndex((s, j) -> vulkanObjects.frameResources[j].renderFinishedSemaphore = s);
        VkSyncObjectHelper.createFences(imageCount).forEachWithIndex((s, j) -> vulkanObjects.frameResources[j].inFlightFence = s);

        vulkanObjects.descriptorPool = VkDescriptorHelper.createDescriptorPool(Engine.getLogicalDevice(), imageCount,
                new VkDescriptorHelper.DescriptorPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 128 * imageCount));
    }

    private int lastImageIndex = 2;
    private FrameData lastFrameData;

    @EventSubscriber(Updater.EVENT_POST_UPDATE)
    public void drawFrame(float deltaTime) {
        IntList entities = getScene().getEntitiesByComponents(Sprite2d.class);
        if (entities.size() == 0) return;

        try (MemoryStack stack = stackPush()) {
            VulkanObjects.FrameResources frameResource = vulkanObjects.frameResources[lastImageIndex == 2 ? 0 : lastImageIndex + 1];

            LongBuffer inFlightFence = stack.longs(frameResource.inFlightFence);
            vkWaitForFences(Engine.getLogicalDevice(), inFlightFence, true, Integer.MAX_VALUE);
            vkResetFences(Engine.getLogicalDevice(), inFlightFence);

            if (lastFrameData != null) {
                vkResetCommandBuffer(frameResource.commandBuffer, 0);
                VkBufferHelper.destroyBuffer(Engine.getLogicalDevice(), lastFrameData.instanceBuffer);
                vkFreeDescriptorSets(Engine.getLogicalDevice(), vulkanObjects.descriptorPool, lastFrameData.descriptorSet);
            }

            IntBuffer pImageIndex = stack.mallocInt(1);
            vkAcquireNextImageKHR(Engine.getLogicalDevice(), Engine.getMainWindow().getVkSwapchain(), 1000000000, frameResource.imageAvailableSemaphore, VK_NULL_HANDLE, pImageIndex);

            LongBuffer waitSemaphores = stack.longs(frameResource.imageAvailableSemaphore);
            IntBuffer waitStages = stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            LongBuffer signalSemaphores = stack.longs(frameResource.renderFinishedSemaphore);

            //region Build frame data
            MutableList<ImageProvider> images = Lists.mutable.empty();
            ByteBuffer instanceData = stack.malloc(InstanceData.BYTES * entities.size());

            entities.forEachWithIndex((e, i) -> {
                ImageProvider image = sprite2d.getSprite(e);
                int spriteIndex;
                if (images.contains(image))
                    spriteIndex = images.indexOf(image);
                else {
                    spriteIndex = images.size();
                    images.add(image);
                }

                Vector2f pos = transform.getPosition(e, new Vector2f());
                Vector2f scale = transform.getScale(e, new Vector2f());
                float rot = transform.getRotation(e);
                // Negate y because joml was made for OpenGL which has an inverted y-axis
                Matrix4f modelMatrix = new Matrix4f().translationRotateScale(new Vector3f(pos.x, -pos.y, 0), new Quaternionf().rotateZ(rot), new Vector3f(scale.x, scale.y, 1));

                Vector2f size = new Vector2f(sprite2d.getSize(e)).div(pixelScale.getPixelsPerUnit());
                new InstanceData(spriteIndex, size, modelMatrix).writeToBuffer(instanceData, i * InstanceData.BYTES);
            });

            FrameData frameData = new FrameData(
                    VkBufferHelper.createFilledBuffer(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, instanceData, null),
                    VkDescriptorHelper.createDescriptorSet(Engine.getLogicalDevice(), vulkanObjects.descriptorPool, vulkanObjects.descriptorSetLayout,
                            new VkDescriptorHelper.DescriptorImageBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 0,
                                    images.stream().map(i -> new VkDescriptorHelper.Image(i.getImageView(), i.getSampler(), VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)).toArray(VkDescriptorHelper.Image[]::new))),
                    entities.size());
            //endregion

            VkCommandBuffer commandBuffer = frameResource.commandBuffer;
            recordCommandBuffer(commandBuffer, frameResource, CameraMatrices.fromCamera(camera, transform, getScene().getEntitiesByComponents(Camera2d.class).getFirst()), frameData);

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(commandBuffer));
            submitInfo.waitSemaphoreCount(1);
            submitInfo.pWaitSemaphores(waitSemaphores);
            submitInfo.pWaitDstStageMask(waitStages);
            submitInfo.pSignalSemaphores(signalSemaphores);

            synchronized (Engine.getGraphicsQueue()) {
                if (vkQueueSubmit(Engine.getGraphicsQueue(), submitInfo, inFlightFence.get(0)) != VK_SUCCESS)
                    throw new RuntimeException("Failed to submit draw command buffer!");
            }

            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack);
            presentInfo.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);
            presentInfo.pWaitSemaphores(signalSemaphores);
            LongBuffer swapChains = stack.longs(Engine.getMainWindow().getVkSwapchain());
            presentInfo.swapchainCount(1);
            presentInfo.pSwapchains(swapChains);
            presentInfo.pImageIndices(pImageIndex);

            vkQueuePresentKHR(Engine.getPresentQueue(), presentInfo);

            lastImageIndex = pImageIndex.get(0);
            lastFrameData = frameData;
        }
    }

    private void recordCommandBuffer(VkCommandBuffer commandBuffer, VulkanObjects.FrameResources frameResources, BufferWritable pushConstants, FrameData frameData) {
        try (MemoryStack stack = stackPush()) {
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
            renderPassInfo.framebuffer(frameResources.framebuffer);
            renderPassInfo.renderArea(renderArea);
            renderPassInfo.pClearValues(clearValues);

            vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE);
            {
                vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, vulkanObjects.graphicsPipeline.pipeline());

                LongBuffer vertexBuffers = stack.longs(frameData.instanceBuffer.buffer());
                LongBuffer offsets = stack.longs(0);
                vkCmdBindVertexBuffers(commandBuffer, 0, vertexBuffers, offsets);

                vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, vulkanObjects.graphicsPipeline.pipelineLayout(), 0, stack.longs(frameData.descriptorSet), null);

                ByteBuffer pushConstantBuffer = stack.malloc(pushConstants.bytes());
                pushConstants.writeToBuffer(pushConstantBuffer, 0);
                vkCmdPushConstants(commandBuffer, vulkanObjects.graphicsPipeline.pipelineLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0, pushConstantBuffer);

                vkCmdDraw(commandBuffer, 6, frameData.instanceCount, 0, 0);
            }
            vkCmdEndRenderPass(commandBuffer);

            if (vkEndCommandBuffer(commandBuffer) != VK_SUCCESS)
                throw new RuntimeException("Failed to record command buffer");
        }
    }
}
