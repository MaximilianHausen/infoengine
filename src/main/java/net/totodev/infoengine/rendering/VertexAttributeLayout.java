package net.totodev.infoengine.rendering;

import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.lwjgl.vulkan.*;

import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

public class VertexAttributeLayout {
    MutableIntList attributes = IntLists.mutable.empty();
    MutableIntList attributeSizes = IntLists.mutable.empty();

    /**
     * @param format The format of the attribute to add
     * @param formatSize The size of the specified format in bytes (Look it up on <a href="www.khronos.org/registry/vulkan/specs/1.3-extensions/man/html/VkFormat.html">VkFormat Documentation</a>)
     */
    public void addAttribute(int format, int formatSize) {
        attributes.add(format);
        attributes.add(formatSize);
    }

    private VkVertexInputBindingDescription.Buffer buildBindingDescription() {
        VkVertexInputBindingDescription.Buffer bindingDescription =
                VkVertexInputBindingDescription.calloc(1);

        bindingDescription.binding(0);
        bindingDescription.stride((int) attributeSizes.sum());
        bindingDescription.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        return bindingDescription;
    }

    public VkVertexInputAttributeDescription.Buffer buildAttributeDescriptions() {
        int attributeCount = attributes.count(i -> true);

        VkVertexInputAttributeDescription.Buffer attributeDescriptions =
                VkVertexInputAttributeDescription.calloc(attributeCount);

        int currentOffset = 0;

        for (int i = 0; i < attributeCount; i++) {
            VkVertexInputAttributeDescription posDescription = attributeDescriptions.get();
            posDescription.binding(0);
            posDescription.location(0);
            posDescription.format(attributes.get(i));
            posDescription.offset(currentOffset);

            currentOffset += attributeSizes.get(i);
        }

        return attributeDescriptions.rewind();
    }
}
