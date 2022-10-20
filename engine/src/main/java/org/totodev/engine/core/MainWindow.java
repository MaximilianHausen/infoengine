package org.totodev.engine.core;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.*;
import org.totodev.engine.rendering.VkBuilder;
import org.totodev.engine.rendering.vulkan.VkLogicalDeviceHelper;
import org.totodev.vulkan.QueueFamilies;

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

        VkPhysicalDeviceDescriptorIndexingFeaturesEXT indexingFeatures = VkPhysicalDeviceDescriptorIndexingFeaturesEXT.calloc()
                .sType(VK13.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES)
                .runtimeDescriptorArray(true)
                .descriptorBindingPartiallyBound(true);

        VkLogicalDeviceHelper.LogicalDeviceCreationResult deviceCreationResult = VkLogicalDeviceHelper.createLogicalDevice(Engine.getPhysicalDevice(), getVkSurface(), Engine.VULKAN_EXTENSIONS,
                deviceFeatures -> {
                    deviceFeatures.samplerAnisotropy(true);
                }, indexingFeatures.address());
        Engine.setLogicalDevice(deviceCreationResult.device());
        Engine.setGraphicsQueue(deviceCreationResult.graphicsQueue());
        Engine.setPresentQueue(deviceCreationResult.presentQueue());

        super.initVulkan();
    }
}
