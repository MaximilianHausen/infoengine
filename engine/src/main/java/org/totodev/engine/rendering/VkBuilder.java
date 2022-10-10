package org.totodev.engine.rendering;

import org.totodev.engine.core.Engine;
import org.totodev.vulkan.*;

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
}
