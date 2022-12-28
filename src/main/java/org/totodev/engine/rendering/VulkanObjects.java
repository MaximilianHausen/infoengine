package org.totodev.engine.rendering;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.totodev.engine.core.Engine;
import org.totodev.engine.ecs.GlobalComponent;
import org.totodev.engine.rendering.vulkan.VkPipelineHelper;

public class VulkanObjects implements GlobalComponent {
    public static class FrameResources {
        public long imageAvailableSemaphore;
        public long renderFinishedSemaphore;
        public long inFlightFence;

        public long framebuffer;

        public VkCommandBuffer commandBuffer;
    }

    public VulkanObjects() {
        frameResources = new FrameResources[Engine.getMainWindow().getVkImages().length];
        for (int i = 0; i < frameResources.length; i++)
            frameResources[i] = new FrameResources();
    }

    public final FrameResources[] frameResources;

    public long commandPool;

    public long descriptorSetLayout;
    public long descriptorPool;

    public long renderPass;
    public VkPipelineHelper.VkPipeline graphicsPipeline;

    public String serializeState() {
        return null;
    }
    public void deserializeState(String data) {
    }
}
