package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.*;
import net.totodev.infoengine.ecs.*;
import net.totodev.infoengine.rendering.vulkan.*;
import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;

public class DebugSquareRenderer implements ISystem {
    public long vertexBuffer;
    public long vertexBufferMemory;
    public long indexBuffer;
    public long indexBufferMemory;

    public void initialize(Scene scene) {
        RendererConfig rendererConfig = scene.getGlobalComponent(RendererConfig.class);
        if (rendererConfig == null) scene.addGlobalComponent(new RendererConfig());
        rendererConfig = scene.getGlobalComponent(RendererConfig.class);

        Window window = Engine.getMainWindow();

        rendererConfig.renderPass = VkRenderPassHelper.createRenderPass(window.getVkImageFormat());
        rendererConfig.graphicsPipeline = VkGraphicsPipelineHelper.createGraphicsPipeline(window.getVkExtent(), rendererConfig.renderPass);
        rendererConfig.framebuffers.addAll(VkFramebufferHelper.createFramebuffers(window.getVkImageViews(), window.getVkExtent().width(), window.getVkExtent().height(), rendererConfig.renderPass));
        rendererConfig.commandPool = VkCommandBufferHelper.createCommandPool(Engine.getGraphicsQueueFamily());

        try (MemoryStack stack = stackPush()) {
            ByteBuffer vertexData = stack.malloc(8 * 4);
            vertexData.asFloatBuffer().put(new float[]{-0.1f, -0.1f, 0.1f, -0.1f, 0.1f, 0.1f, -0.1f, 0.1f});
            ByteBuffer indexData = stack.malloc(6 * 2);
            indexData.asShortBuffer().put(new short[]{0, 1, 2, 2, 3, 0});

            LongBuffer pBuffer = stack.callocLong(1);
            LongBuffer pBufferMemory = stack.callocLong(1);

            VkBufferHelper.createVertexBuffer(rendererConfig.commandPool, vertexData, null, pBuffer, pBufferMemory);
            vertexBuffer = pBuffer.get(0);
            vertexBufferMemory = pBufferMemory.get(0);

            VkBufferHelper.createIndexBuffer(rendererConfig.commandPool, indexData, null, pBuffer, pBufferMemory);
            indexBuffer = pBuffer.get(0);
            indexBufferMemory = pBufferMemory.get(0);
        }

        rendererConfig.commandBuffers.addAll(VkCommandBufferHelper.createCommandBuffers(rendererConfig.commandPool, window.getVkImages().count(l -> true)));
    }
    public void start() {

    }
    public void deinitialize(Scene scene) {

    }
}
