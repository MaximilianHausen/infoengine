package net.totodev.infoengine.core;

import net.totodev.infoengine.rendering.vulkan.*;
import org.jetbrains.annotations.NotNull;

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

        VkLogicalDeviceHelper.LogicalDeviceCreationResult deviceCreationResult = VkLogicalDeviceHelper.createLogicalDevice(Engine.getPhysicalDevice(), getVkSurface(), Engine.VULKAN_EXTENSIONS);
        Engine.setLogicalDevice(deviceCreationResult.device());
        Engine.setGraphicsQueue(deviceCreationResult.graphicsQueue());
        Engine.setPresentQueue(deviceCreationResult.presentQueue());

        super.initVulkan();
    }
}
