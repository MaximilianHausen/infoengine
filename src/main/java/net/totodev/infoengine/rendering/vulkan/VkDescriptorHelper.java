package net.totodev.infoengine.rendering.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkDescriptorHelper {
    /**
     * @param binding         The binding number set in the shader
     * @param descriptorType  The type of this descriptor. See: {@link VK10#VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER}
     * @param descriptorCount The amount of descriptors in this binding, usually 1
     * @param shaderStages    Bitmask of the shader stages this binding should be accessible in. See: {@link VK10#VK_SHADER_STAGE_ALL}
     */
    public record DescriptorBindingInfo(int binding, int descriptorType, int descriptorCount, int shaderStages) {
    }
    public static long createDescriptorSetLayout(VkDevice device, DescriptorBindingInfo... bindings) {
        try (MemoryStack stack = stackPush()) {
            VkDescriptorSetLayoutBinding.Buffer vkBindings = VkDescriptorSetLayoutBinding.calloc(bindings.length, stack);
            for (int i = 0; i < bindings.length; i++) {
                DescriptorBindingInfo binding = bindings[i];

                VkDescriptorSetLayoutBinding vkBinding = vkBindings.get(i);
                vkBinding.binding(binding.binding);
                vkBinding.descriptorType(binding.descriptorType);
                vkBinding.descriptorCount(binding.descriptorCount);
                vkBinding.stageFlags(binding.shaderStages);
            }

            VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack);
            layoutInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO);
            layoutInfo.pBindings(vkBindings);

            LongBuffer pDescriptorSetLayout = stack.mallocLong(1);
            if (vkCreateDescriptorSetLayout(device, layoutInfo, null, pDescriptorSetLayout) != VK_SUCCESS)
                throw new RuntimeException("Failed to create descriptor set layout");
            return pDescriptorSetLayout.get(0);
        }
    }

    /**
     * @param type  The type of the descriptor. See: {@link VK10#VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER}
     * @param count How often this descriptor is allocated in the pool. Example: If each descriptor set contains this descriptor once, this would be the max number of sets allocated from the pool.
     */
    public record DescriptorPoolSize(int type, int count) {
    }
    public static long createDescriptorPool(VkDevice device, int maxSets, DescriptorPoolSize... poolSizes) {
        try (MemoryStack stack = stackPush()) {
            VkDescriptorPoolSize.Buffer vkPoolSizes = VkDescriptorPoolSize.calloc(poolSizes.length, stack);
            for (int i = 0; i < poolSizes.length; i++) {
                DescriptorPoolSize poolSize = poolSizes[i];

                VkDescriptorPoolSize vkPoolSize = vkPoolSizes.get(i);
                vkPoolSize.type(poolSize.type);
                vkPoolSize.descriptorCount(poolSize.count);
            }

            VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO);
            poolInfo.pPoolSizes(vkPoolSizes);
            poolInfo.maxSets(maxSets);

            LongBuffer pDescriptorPool = stack.mallocLong(1);
            if (vkCreateDescriptorPool(device, poolInfo, null, pDescriptorPool) != VK_SUCCESS)
                throw new RuntimeException("Failed to create descriptor pool");
            return pDescriptorPool.get(0);
        }
    }

    public record BufferRegion(long buffer, long offset, long size) {
    }
    public record DescriptorBufferBinding(int binding, int descriptorType, int arrayOffset, BufferRegion... buffers) {
    }
    public static long createDescriptorSet(VkDevice device, long descriptorPool, long descriptorSetLayout, DescriptorBufferBinding... bindings) {
        try (MemoryStack stack = stackPush()) {
            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
            allocInfo.descriptorPool(descriptorPool);
            allocInfo.pSetLayouts(stack.longs(descriptorSetLayout));

            LongBuffer pDescriptorSet = stack.mallocLong(1);
            if (vkAllocateDescriptorSets(device, allocInfo, pDescriptorSet) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate descriptor sets");
            long descriptorSet = pDescriptorSet.get(0);

            VkWriteDescriptorSet.Buffer descriptorWrites = VkWriteDescriptorSet.calloc(bindings.length, stack);
            for (int i = 0; i < bindings.length; i++) {
                DescriptorBufferBinding binding = bindings[i];

                VkDescriptorBufferInfo.Buffer bufferInfos = VkDescriptorBufferInfo.calloc(binding.buffers().length, stack);
                for (int j = 0; j < binding.buffers().length; j++) {
                    BufferRegion bufferRegion = binding.buffers()[j];
                    VkDescriptorBufferInfo bufferInfo = bufferInfos.get(j);
                    bufferInfo.buffer(bufferRegion.buffer());
                    bufferInfo.offset(bufferRegion.offset());
                    bufferInfo.range(bufferRegion.size());
                }

                VkWriteDescriptorSet descriptorWrite = descriptorWrites.get(i);
                descriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
                descriptorWrite.dstSet(descriptorSet);
                descriptorWrite.pBufferInfo(bufferInfos);

                descriptorWrite.dstBinding(binding.binding());
                descriptorWrite.dstArrayElement(binding.arrayOffset());
                descriptorWrite.descriptorType(binding.descriptorType());
                descriptorWrite.descriptorCount(binding.buffers().length);
            }

            vkUpdateDescriptorSets(device, descriptorWrites, null);

            return descriptorSet;
        }
    }
}
