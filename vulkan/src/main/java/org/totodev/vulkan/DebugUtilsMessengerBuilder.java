package org.totodev.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

public class DebugUtilsMessengerBuilder {
    private VkInstance instance;
    private VkDebugUtilsMessengerCallbackEXTI callback;
    private int severities = VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
    private int types = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;

    public DebugUtilsMessengerBuilder instance(VkInstance instance) {
        this.instance = instance;
        return this;
    }

    public DebugUtilsMessengerBuilder callback(VkDebugUtilsMessengerCallbackEXTI callback) {
        this.callback = callback;
        return this;
    }

    /**
     * @see org.lwjgl.vulkan.EXTDebugUtils#VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT
     */
    public DebugUtilsMessengerBuilder severities(int severities) {
        this.severities = severities;
        return this;
    }

    /**
     * @see org.lwjgl.vulkan.EXTDebugUtils#VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT
     */
    public DebugUtilsMessengerBuilder types(int types) {
        this.types = types;
        return this;
    }

    public long build() {
        try (MemoryStack stack = stackPush()) {
            VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
            debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
            debugCreateInfo.messageSeverity(severities);
            debugCreateInfo.messageType(types);
            debugCreateInfo.pfnUserCallback(callback);

            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);
            if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") == NULL || vkCreateDebugUtilsMessengerEXT(instance, debugCreateInfo, null, pDebugMessenger) != VK_SUCCESS)
                throw new RuntimeException("Failed to create debug messenger");

            return pDebugMessenger.get(0);
        }
    }
}
