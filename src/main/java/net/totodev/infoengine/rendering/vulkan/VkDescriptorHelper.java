package net.totodev.infoengine.rendering.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.*;
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
        //TODO: Bindless flags
        try (MemoryStack stack = stackPush()) {
            VkDescriptorSetLayoutBinding.Buffer vkBindings = VkDescriptorSetLayoutBinding.calloc(bindings.length, stack);

            for (int i = 0; i < bindings.length; i++) {
                DescriptorBindingInfo binding = bindings[i];
                vkBindings.get(i)
                        .binding(binding.binding)
                        .descriptorType(binding.descriptorType)
                        .descriptorCount(binding.descriptorCount)
                        .stageFlags(binding.shaderStages);
            }

            VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                    .pBindings(vkBindings);

            LongBuffer pDescriptorSetLayout = stack.callocLong(1);
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
            poolInfo.flags(VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT);

            LongBuffer pDescriptorPool = stack.mallocLong(1);
            if (vkCreateDescriptorPool(device, poolInfo, null, pDescriptorPool) != VK_SUCCESS)
                throw new RuntimeException("Failed to create descriptor pool");
            return pDescriptorPool.get(0);
        }
    }


    public abstract static class DescriptorBinding {
        public int binding;
        public int descriptorType;
        public int arrayOffset;

        public BufferRegion[] buffers;
        public Image[] images;
    }

    public record BufferRegion(long buffer, long offset, long size) {
    }

    public static class DescriptorBufferBinding extends DescriptorBinding {
        public DescriptorBufferBinding(int binding, int descriptorType, int arrayOffset, BufferRegion... buffers) {
            super.binding = binding;
            super.descriptorType = descriptorType;
            super.arrayOffset = arrayOffset;
            super.buffers = buffers;
        }
    }

    public record Image(long imageView, long sampler, int imageLayout) {
    }

    public static class DescriptorImageBinding extends DescriptorBinding {
        public DescriptorImageBinding(int binding, int descriptorType, int arrayOffset, Image... images) {
            super.binding = binding;
            super.descriptorType = descriptorType;
            super.arrayOffset = arrayOffset;
            super.images = images;
        }
    }

    public static long createDescriptorSet(VkDevice device, long descriptorPool, long descriptorSetLayout, DescriptorBinding... bindings) {
        try (MemoryStack stack = stackPush()) {
            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
            allocInfo.descriptorPool(descriptorPool);
            allocInfo.pSetLayouts(stack.longs(descriptorSetLayout));

            /*VkDescriptorSetVariableDescriptorCountAllocateInfo count = VkDescriptorSetVariableDescriptorCountAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_VARIABLE_DESCRIPTOR_COUNT_ALLOCATE_INFO)
                    .pDescriptorCounts(stack.ints(128));
            allocInfo.pNext(count);*/

            LongBuffer pDescriptorSet = stack.mallocLong(1);
            if (vkAllocateDescriptorSets(device, allocInfo, pDescriptorSet) != VK_SUCCESS)
                throw new RuntimeException("Failed to allocate descriptor sets");
            long descriptorSet = pDescriptorSet.get(0);

            VkWriteDescriptorSet.Buffer descriptorWrites = VkWriteDescriptorSet.calloc(bindings.length, stack);
            for (int i = 0; i < bindings.length; i++) {
                switch (bindings[i].descriptorType) {
                    case VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER ->
                            fillBufferDescriptorWrite(descriptorWrites.get(i), descriptorSet, bindings[i]);
                    case VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER ->
                            fillImageDescriptorWrite(descriptorWrites.get(i), descriptorSet, bindings[i]);
                }
            }

            vkUpdateDescriptorSets(device, descriptorWrites, null);

            return descriptorSet;
        }
    }

    private static void fillBufferDescriptorWrite(VkWriteDescriptorSet descriptorWrite, long descriptorSet, DescriptorBinding binding) {
        MemoryStack stack = stackGet();

        VkDescriptorBufferInfo.Buffer bufferInfos = VkDescriptorBufferInfo.calloc(binding.buffers.length, stack);
        for (int i = 0; i < binding.buffers.length; i++) {
            BufferRegion bufferRegion = binding.buffers[i];
            VkDescriptorBufferInfo bufferInfo = bufferInfos.get(i);
            bufferInfo.buffer(bufferRegion.buffer());
            bufferInfo.offset(bufferRegion.offset());
            bufferInfo.range(bufferRegion.size());
        }

        descriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
        descriptorWrite.dstSet(descriptorSet);
        descriptorWrite.pBufferInfo(bufferInfos);

        descriptorWrite.dstBinding(binding.binding);
        descriptorWrite.dstArrayElement(binding.arrayOffset);
        descriptorWrite.descriptorType(binding.descriptorType);
        descriptorWrite.descriptorCount(binding.buffers.length);
    }

    private static void fillImageDescriptorWrite(VkWriteDescriptorSet descriptorWrite, long descriptorSet, DescriptorBinding binding) {
        MemoryStack stack = stackGet();

        VkDescriptorImageInfo.Buffer imageInfos = VkDescriptorImageInfo.calloc(binding.images.length, stack);
        for (int i = 0; i < binding.images.length; i++) {
            Image image = binding.images[i];
            VkDescriptorImageInfo imageInfo = imageInfos.get(i);
            imageInfo.imageView(image.imageView());
            imageInfo.sampler(image.sampler());
            imageInfo.imageLayout(image.imageLayout());
        }

        descriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
        descriptorWrite.dstSet(descriptorSet);
        descriptorWrite.pImageInfo(imageInfos);

        descriptorWrite.dstBinding(binding.binding);
        descriptorWrite.dstArrayElement(binding.arrayOffset);
        descriptorWrite.descriptorType(binding.descriptorType);
        descriptorWrite.descriptorCount(binding.images.length);
    }
}
