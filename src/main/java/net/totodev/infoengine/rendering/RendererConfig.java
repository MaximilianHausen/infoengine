package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.IGlobalComponent;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.lwjgl.vulkan.VkCommandBuffer;

public class RendererConfig implements IGlobalComponent {
    public long renderPass;
    public long graphicsPipeline;
    public final MutableLongList framebuffers = LongLists.mutable.empty();
    public long commandPool;
    public final MutableList<VkCommandBuffer> commandBuffers = Lists.mutable.empty();

    public MutableLongList imageAvailableSemaphores;
    public MutableLongList renderFinishedSemaphores;
    public MutableLongList inFlightFences;

    public String serializeState() {
        return null;
    }
    public void deserializeState(String data) {

    }
}
