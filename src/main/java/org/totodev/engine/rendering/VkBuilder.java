package org.totodev.engine.rendering;

import org.joml.Vector2i;
import org.totodev.engine.core.*;
import org.totodev.engine.vulkan.*;

/**
 * Factory for different vulkan builders with engine-specific boilerplate values being set automatically.
 */
public class VkBuilder {
    public static InstanceBuilder instance() {
        return new InstanceBuilder();
    }

    public static DebugUtilsMessengerBuilder debugUtilsMessenger() {
        return new DebugUtilsMessengerBuilder().instance(Engine.getVkInstance());
    }

    public static PhysicalDevicePicker physicalDevice() {
        return new PhysicalDevicePicker().instance(Engine.getVkInstance());
    }

    public static LogicalDeviceBuilder logicalDevice() {
        return new LogicalDeviceBuilder();
    }

    public static SwapchainBuilder swapchain() {
        return new SwapchainBuilder().device(Engine.getLogicalDevice());
    }
    public static SwapchainBuilder swapchain(Window window) {
        Vector2i size = window.getFramebufferSize();
        return new SwapchainBuilder()
            .device(Engine.getLogicalDevice())
            .surface(window.getVkSurface())
            .size(size.x, size.y);
    }
}
