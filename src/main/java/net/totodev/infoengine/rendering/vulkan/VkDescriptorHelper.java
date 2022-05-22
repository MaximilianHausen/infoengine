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
     * @param descriptorCount The amount of descriptors in this binding if this binding is an array, otherwise 1
     * @param shaderStages    Bitmask of the shader stages this binding should be accessible in. See: {@link VK10#VK_SHADER_STAGE_ALL}
     */
    public record DescriptorBinding(int binding, int descriptorType, int descriptorCount, int shaderStages) {
    }
    public static long createDescriptorSetLayout(VkDevice device, DescriptorBinding... bindings) {
        try (MemoryStack stack = stackPush()) {
            VkDescriptorSetLayoutBinding.Buffer vkBindings = VkDescriptorSetLayoutBinding.calloc(bindings.length, stack);
            for (int i = 0; i < bindings.length; i++) {
                DescriptorBinding binding = bindings[i];

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

    //TODO: Finish createDescriptorSets
    /*public static MutableLongList createDescriptorSets(VkDevice device, long descriptorPool, long descriptorSetLayout, int amount) {
        try (MemoryStack stack = stackPush()) {
            // Useless copies to allocate multiple
            LongBuffer layouts = stack.mallocLong(amount);
            for (int i = 0; i < amount; i++)
                layouts.put(i, descriptorSetLayout);

            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
            allocInfo.descriptorPool(descriptorPool);
            allocInfo.pSetLayouts(layouts);

            LongBuffer pDescriptorSets = stack.mallocLong(amount);
            if (vkAllocateDescriptorSets(device, allocInfo, pDescriptorSets) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate descriptor sets");

            MutableLongList descriptorSets = LongLists.mutable.empty();

            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1, stack);
            bufferInfo.offset(0);
            bufferInfo.range(UniformBufferObject.SIZEOF);

            VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.calloc(1, stack);
            descriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
            descriptorWrite.dstBinding(0);
            descriptorWrite.dstArrayElement(0);
            descriptorWrite.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
            descriptorWrite.descriptorCount(1);
            descriptorWrite.pBufferInfo(bufferInfo);

            for (int i = 0; i < amount; i++) {
                long descriptorSet = pDescriptorSets.get(i);

                bufferInfo.buffer(uniformBuffers.get(i));

                descriptorWrite.dstSet(descriptorSet);

                vkUpdateDescriptorSets(device, descriptorWrite, null);

                descriptorSets.add(descriptorSet);
            }

            return descriptorSets;
        }
    }*/
}
