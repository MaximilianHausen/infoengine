package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.Engine;
import net.totodev.infoengine.ecs.IGlobalComponent;
import net.totodev.infoengine.rendering.vulkan.VkPipelineHelper;
import org.eclipse.collections.api.factory.Lists;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.util.Arrays;

public class VulkanObjects implements IGlobalComponent {
    public static class FrameData {
        public long imageAvailableSemaphore;
        public long renderFinishedSemaphore;
        public long inFlightFence;

        public long framebuffer;

        public long descriptorSet;

        public VkCommandBuffer commandBuffer;
    }

    public VulkanObjects() {
        frameData = new FrameData[Engine.getMainWindow().getVkImages().size()];
        for (int i = 0; i < frameData.length; i++)
            frameData[i] = new FrameData();
    }

    public final FrameData[] frameData;

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
