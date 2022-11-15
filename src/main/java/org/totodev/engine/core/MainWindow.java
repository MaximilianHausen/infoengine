package org.totodev.engine.core;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.rendering.VkBuilder;
import org.totodev.engine.vulkan.QueueFamilies;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;

/**
 * A window with all the first-time vulkan setup that depends on a surface
 */
public class MainWindow extends Window {
    public MainWindow(@NotNull String title, int width, int height, boolean startHidden) {
        super(title, width, height, startHidden);
    }

    @Override
    protected void initVulkan() {
        Engine.setPhysicalDevice(VkBuilder.physicalDevice()
                .extensions(Engine.VULKAN_EXTENSIONS)
                .features((features) -> true)
                .queueFamilies((families) -> families.findQueueFamily(VK_QUEUE_GRAPHICS_BIT, null) != null && families.findQueueFamily(0, getVkSurface()) != null)
                .pick());

        QueueFamilies queueFamilies = new QueueFamilies(Engine.getPhysicalDevice());
        Engine.setGraphicsQueueFamily(queueFamilies.findQueueFamily(VK_QUEUE_GRAPHICS_BIT, null).index());
        Engine.setPresentQueueFamily(queueFamilies.findQueueFamily(0, getVkSurface()).index());

        try (MemoryStack stack = stackPush()) {
            VkPhysicalDeviceDescriptorIndexingFeaturesEXT indexingFeatures = VkPhysicalDeviceDescriptorIndexingFeaturesEXT.calloc(stack)
                    .sType(VK13.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES)
                    .runtimeDescriptorArray(true)
                    .descriptorBindingPartiallyBound(true);

            Engine.setLogicalDevice(VkBuilder.logicalDevice()
                    .physicalDevice(Engine.getPhysicalDevice())
                    .extensions(Engine.VULKAN_EXTENSIONS)
                    .features(features -> features.samplerAnisotropy(true))
                    .pNext(indexingFeatures.address())
                    .build());
        }

        //TODO: Queue creation
        //Engine.setGraphicsQueue(deviceCreationResult.graphicsQueue());
        //Engine.setPresentQueue(deviceCreationResult.presentQueue());

        super.initVulkan();
    }
}
