package net.totodev.engine.rendering.vulkan;

import net.totodev.engine.util.logging.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

public final class VkDebugUtilsHelper {
    public static long createDebugMessenger(VkInstance instance) {
        return createDebugMessenger(instance, VkDebugUtilsHelper::loggingDebugCallback);
    }

    public static long createDebugMessenger(VkInstance instance, VkDebugUtilsMessengerCallbackEXTI callback) {
        try (MemoryStack stack = stackPush()) {
            VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
            populateDebugMessengerCreateInfo(createInfo, callback);

            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);
            if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL)
                vkCreateDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger);
            else throw new RuntimeException("Failed to set up debug messenger");

            return pDebugMessenger.get(0);
        }
    }
    private static void destroyDebugUtilsMessengerEXT(VkInstance instance, long debugMessenger, VkAllocationCallbacks allocationCallbacks) {
        if (vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL)
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
    }

    public static void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo, VkDebugUtilsMessengerCallbackEXTI callback) {
        debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
        debugCreateInfo.pfnUserCallback(callback);
    }

    public static int loggingDebugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

        LogLevel logLevel = switch (messageSeverity) {
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> LogLevel.Trace;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> LogLevel.Info;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> LogLevel.Error;
            default -> LogLevel.Debug;
        };
        Logger.log(logLevel, "VkLayer", callbackData.pMessageString());

        return VK_FALSE;
    }
}
