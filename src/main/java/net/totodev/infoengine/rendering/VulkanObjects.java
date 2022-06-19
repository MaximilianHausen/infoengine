package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.IGlobalComponent;
import net.totodev.infoengine.rendering.vulkan.VkPipelineHelper;
import org.lwjgl.vulkan.VkCommandBuffer;

public class VulkanObjects implements IGlobalComponent {
    public static class FrameData {
        public long imageAvailableSemaphore;
        public long renderFinishedSemaphore;
        public long inFlightFence;

        public long framebuffer;

        public long descriptorSet;

        public VkCommandBuffer commandBuffer;
    }

    public final FrameData[] frameData = new FrameData[]{new FrameData(), new FrameData()};

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
