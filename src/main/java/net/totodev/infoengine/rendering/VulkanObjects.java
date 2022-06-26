package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.Engine;
import net.totodev.infoengine.ecs.GlobalComponent;
import net.totodev.infoengine.rendering.vulkan.VkPipelineHelper;
import org.lwjgl.vulkan.VkCommandBuffer;

public class VulkanObjects implements GlobalComponent {
    public static class FrameResources {
        public long imageAvailableSemaphore;
        public long renderFinishedSemaphore;
        public long inFlightFence;

        public long framebuffer;

        public VkCommandBuffer commandBuffer;
    }

    public VulkanObjects() {
        frameResources = new FrameResources[Engine.getMainWindow().getVkImages().size()];
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
