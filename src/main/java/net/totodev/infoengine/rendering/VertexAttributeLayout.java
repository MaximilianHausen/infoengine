package net.totodev.infoengine.rendering;

import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.lwjgl.vulkan.*;

import static org.lwjgl.system.MemoryStack.stackGet;

public class VertexAttributeLayout {
    private final MutableIntList attributes = IntLists.mutable.empty();
    private final MutableIntList attributeSizes = IntLists.mutable.empty();

    private final int inputRate;

    public VertexAttributeLayout(int inputRate) {
        this.inputRate = inputRate;
    }

    /**
     * @param format The format of the attribute to add
     * @param formatSize The size of the specified format in bytes (Look it up at <a href="https://khronos.org/registry/vulkan/specs/1.3-extensions/man/html/VkFormat.html#_description">VkFormat Documentation</a>)
     */
    public VertexAttributeLayout addAttribute(int format, int formatSize) {
        attributes.add(format);
        attributeSizes.add(formatSize);
        return this;
    }

    public VkVertexInputBindingDescription.Buffer buildBindingDescription() {
        VkVertexInputBindingDescription.Buffer bindingDescription =
                VkVertexInputBindingDescription.calloc(1, stackGet());

        bindingDescription.binding(0);
        bindingDescription.stride((int) attributeSizes.sum());
        bindingDescription.inputRate(inputRate);

        return bindingDescription;
    }

    public VkVertexInputAttributeDescription.Buffer buildAttributeDescriptions() {
        int attributeCount = attributes.size();

        VkVertexInputAttributeDescription.Buffer attributeDescriptions =
                VkVertexInputAttributeDescription.calloc(attributeCount, stackGet());

        int currentOffset = 0;

        for (int i = 0; i < attributeCount; i++) {
            VkVertexInputAttributeDescription posDescription = attributeDescriptions.get();
            posDescription.binding(0);
            posDescription.location(i);
            posDescription.format(attributes.get(i));
            posDescription.offset(currentOffset);

            currentOffset += attributeSizes.get(i);
        }

        return attributeDescriptions.rewind();
    }
}
