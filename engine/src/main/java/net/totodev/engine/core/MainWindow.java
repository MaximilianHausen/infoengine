package net.totodev.engine.core;

import net.totodev.engine.rendering.vulkan.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.*;

/**
 * A window with all the first-time vulkan setup that depends on a surface
 */
public class MainWindow extends Window {
    public MainWindow(@NotNull String title, int width, int height, boolean startHidden) {
        super(title, width, height, startHidden);
    }

    @Override
    protected void initVulkan() {
        Engine.setPhysicalDevice(VkPhysicalDeviceHelper.pickPhysicalDevice(Engine.getVkInstance(), getVkSurface(), null, Engine.VULKAN_EXTENSIONS));

        VkQueueHelper.QueueFamilyIndices queueFamilies = VkQueueHelper.findQueueFamilies(Engine.getPhysicalDevice(), getVkSurface());
        Engine.setGraphicsQueueFamily(queueFamilies.graphicsFamily);
        Engine.setPresentQueueFamily(queueFamilies.presentFamily);

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
