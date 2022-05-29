package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.IGlobalComponent;
import net.totodev.infoengine.rendering.vulkan.VkPipelineHelper;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.lwjgl.vulkan.VkCommandBuffer;

public class RendererConfig implements IGlobalComponent {
    public long commandPool;
    public final MutableList<VkCommandBuffer> commandBuffers = Lists.mutable.empty();

    public long descriptorSetLayout;
    public long descriptorPool;
    public final MutableLongList descriptorSets = LongLists.mutable.empty();

    public final MutableLongList imageAvailableSemaphores = LongLists.mutable.empty();
    public final MutableLongList renderFinishedSemaphores = LongLists.mutable.empty();
    public final MutableLongList inFlightFences = LongLists.mutable.empty();

    public long renderPass;
    public VkPipelineHelper.VkPipeline graphicsPipeline;
    public final MutableLongList framebuffers = LongLists.mutable.empty();

    public String serializeState() {
        return null;
    }
    public void deserializeState(String data) {

    }
}
