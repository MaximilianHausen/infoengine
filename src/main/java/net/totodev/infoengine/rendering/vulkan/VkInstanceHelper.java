package net.totodev.infoengine.rendering.vulkan;

import net.totodev.infoengine.util.*;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.set.*;
import org.eclipse.collections.impl.factory.Sets;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.vulkan.VK13.*;

public final class VkInstanceHelper {
    public static VkInstance createInstance(String appName, SemVer appVersion, SetIterable<String> validationLayers) {
        return createInstance(appName, appVersion, validationLayers, VkDebugUtilsHelper::loggingDebugCallback);
    }
    public static VkInstance createInstance(String appName, SemVer appVersion, SetIterable<String> validationLayers, VkDebugUtilsMessengerCallbackEXTI debugCallback) {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8Safe(appName))
                    .applicationVersion(VK_MAKE_API_VERSION(0, appVersion.major(), appVersion.minor(), appVersion.patch()))
                    .pEngineName(stack.UTF8Safe("InfoEngine"))
                    .engineVersion(VK_MAKE_VERSION(1, 0, 0))
                    .apiVersion(VK_API_VERSION_1_3);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(appInfo)
                    .ppEnabledExtensionNames(getRequiredExtensions(!validationLayers.isEmpty()))
                    .ppEnabledLayerNames(null);

            addLayers(createInfo, validationLayers);
            if (debugCallback != null)
                addDebugMessenger(createInfo, debugCallback);

            PointerBuffer instancePtr = stack.mallocPointer(1);
            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS)
                throw new RuntimeException("Failed to create instance");

            return new VkInstance(instancePtr.get(0), createInfo);
        }
    }

    public static void addLayers(VkInstanceCreateInfo instanceCreateInfo, SetIterable<String> layers) {
        if (layers.isEmpty()) return;
        MemoryStack stack = stackGet();

        IntBuffer layerCount = stack.ints(0);
        vkEnumerateInstanceLayerProperties(layerCount, null);

        VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);
        vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

        MutableSet<String> availableLayerNames = Sets.mutable.fromStream(
                availableLayers.stream().map(VkLayerProperties::layerNameString));

        if (availableLayerNames.containsAllIterable(layers)) {
            instanceCreateInfo.ppEnabledLayerNames(BufferUtils.asPointerBuffer(layers));
        } else {
            Logger.log(LogLevel.Error, "Vulkan",
                    "The following layers were requested during instance creation but are not supported: "
                            + layers.difference(availableLayerNames).makeString());
        }
    }

    public static void addDebugMessenger(VkInstanceCreateInfo instanceCreateInfo, VkDebugUtilsMessengerCallbackEXTI callback) {
        MemoryStack stack = stackGet();

        VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
        VkDebugUtilsHelper.populateDebugMessengerCreateInfo(debugCreateInfo, callback);

        instanceCreateInfo.pNext(debugCreateInfo.address());
    }

    private static PointerBuffer getRequiredExtensions(boolean useDebugUtils) {
        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();
        // This is a game engine, so I don't care about offscreen rendering
        if (glfwExtensions == null)
            throw new UnsupportedOperationException("Vulkan extensions required for lwjgl not supported");

        if (useDebugUtils) {
            MemoryStack stack = MemoryStack.stackGet();

            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);

            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

            return extensions.rewind();
        }

        return glfwExtensions;
    }
}
