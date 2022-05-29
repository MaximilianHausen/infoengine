package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.rendering.Image;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkImageHelper {
    public record VkImage(long image, long imageMemory) {
    }
    public static VkImage createImage(VkDevice device, VkPhysicalDevice physicalDevice, int width, int height, int format, int tiling, int usage, int memProperties) {
        try (MemoryStack stack = stackPush()) {
            VkImageCreateInfo imageInfo = VkImageCreateInfo.calloc(stack);
            imageInfo.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            imageInfo.imageType(VK_IMAGE_TYPE_2D);
            imageInfo.extent().width(width);
            imageInfo.extent().height(height);
            imageInfo.extent().depth(1);
            imageInfo.mipLevels(1);
            imageInfo.arrayLayers(1);
            imageInfo.format(format);
            imageInfo.tiling(tiling);
            imageInfo.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            imageInfo.usage(usage);
            imageInfo.samples(VK_SAMPLE_COUNT_1_BIT);
            imageInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            LongBuffer pTextureImage = stack.mallocLong(1);
            if (vkCreateImage(device, imageInfo, null, pTextureImage) != VK_SUCCESS)
                throw new RuntimeException("Failed to create image");
            long textureImage = pTextureImage.get(0);

            VkMemoryRequirements memRequirements = VkMemoryRequirements.malloc(stack);
            vkGetImageMemoryRequirements(device, textureImage, memRequirements);
            long textureImageMemory = VkMemoryHelper.allocateMemory(device, physicalDevice, memRequirements, memProperties);

            vkBindImageMemory(device, textureImage, textureImageMemory, 0);

            return new VkImage(textureImage, textureImageMemory);
        }
    }

    public static VkImage createTextureImage(VkDevice device, long commandPool, VkQueue queue, VkPhysicalDevice physicalDevice, Image image) {
        VkBufferHelper.VkBuffer stagingBuffer = VkBufferHelper.createFilledBuffer(device, physicalDevice, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, image.pixels(), null);
        VkImage vkImage = createImage(device, physicalDevice, image.width(), image.height(), VK_FORMAT_R8G8B8A8_SRGB, VK_IMAGE_TILING_OPTIMAL, VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

        transitionImageLayout(device, commandPool, queue, vkImage.image, VK_FORMAT_R8G8B8A8_SRGB, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
        copyBufferToImage(device, queue, commandPool, stagingBuffer.buffer(), vkImage.image, new BufferImageCopyRegion(0, 1, 0, 1, new Vector3i(), new Vector3i(image.width(), image.height(), 1)));

        transitionImageLayout(device, commandPool, queue, vkImage.image, VK_FORMAT_R8G8B8A8_SRGB, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

        return vkImage;
    }

    public static void transitionImageLayout(VkDevice device, long commandPool, VkQueue queue, long image, int format, int oldLayout, int newLayout) {
        try (MemoryStack stack = stackPush()) {
            VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack);
            barrier.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER);
            barrier.oldLayout(oldLayout);
            barrier.newLayout(newLayout);
            barrier.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
            barrier.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
            barrier.image(image);
            barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
            barrier.subresourceRange().baseMipLevel(0);
            barrier.subresourceRange().levelCount(1);
            barrier.subresourceRange().baseArrayLayer(0);
            barrier.subresourceRange().layerCount(1);

            int sourceStage;
            int destinationStage;

            if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
                barrier.srcAccessMask(0);
                barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);

                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
                destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
            } else if (oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
                barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
                barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);

                sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
                destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
            } else
                throw new IllegalArgumentException("Unsupported layout transition");

            VkCommandBuffer commandBuffer = VkCommandBufferHelper.beginSingleTimeCommands(device, commandPool);
            {
                vkCmdPipelineBarrier(commandBuffer,
                        sourceStage, destinationStage,
                        0,
                        null,
                        null,
                        barrier);
            }
            VkCommandBufferHelper.endSingleTimeCommands(device, commandPool, commandBuffer, queue);
        }
    }

    public record BufferImageCopyRegion(long srcBufferOffset, int dstMipLevel, int dstArrayLayer,
                                        int dstArrayLayerCount, Vector3i dstImageOffset, Vector3i dstImageSize) {
    }
    public static void copyBufferToImage(VkDevice device, VkQueue transferQueue, long commandPool, long srcBuffer, long dstImage, BufferImageCopyRegion... regions) {
        try (MemoryStack stack = stackPush()) {
            VkCommandBuffer commandBuffer = VkCommandBufferHelper.beginSingleTimeCommands(device, commandPool);
            {
                VkBufferImageCopy.Buffer copyRegions = VkBufferImageCopy.calloc(regions.length, stack);
                for (int i = 0; i < regions.length; i++) {
                    BufferImageCopyRegion region = regions[i];
                    VkBufferImageCopy copyRegion = copyRegions.get(i);
                    copyRegion.bufferOffset(region.srcBufferOffset);
                    copyRegion.bufferRowLength(0);
                    copyRegion.bufferImageHeight(0);

                    copyRegion.imageSubresource().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                    copyRegion.imageSubresource().mipLevel(region.dstMipLevel);
                    copyRegion.imageSubresource().baseArrayLayer(region.dstArrayLayer);
                    copyRegion.imageSubresource().layerCount(region.dstArrayLayerCount);

                    copyRegion.imageOffset(VkOffset3D.malloc(stack).set(region.dstImageOffset.x, region.dstImageOffset.y, region.dstImageOffset.z));
                    copyRegion.imageExtent(VkExtent3D.malloc(stack).set(region.dstImageSize.x, region.dstImageSize.y, region.dstImageSize.z));
                }

                vkCmdCopyBufferToImage(commandBuffer, srcBuffer, dstImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, copyRegions);
            }
            VkCommandBufferHelper.endSingleTimeCommands(device, commandPool, commandBuffer, transferQueue);
        }
    }


}
