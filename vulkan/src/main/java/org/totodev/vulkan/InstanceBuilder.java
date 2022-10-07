package org.totodev.vulkan;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK12.*;

public class InstanceBuilder {
    private VkApplicationInfo appInfo;
    private ImmutableSet<String> layers;
    private ImmutableSet<String> extensions;
    private VkDebugUtilsMessengerCallbackEXTI debugCallback;

    public InstanceBuilder appInfo(String appName, SemVer appVersion, String engineName, SemVer engineVersion) {
        appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(memUTF8(appName))
                .applicationVersion(appVersion.asVkVersion())
                .pEngineName(memUTF8(engineName))
                .engineVersion(engineVersion.asVkVersion())
                .apiVersion(VK_API_VERSION_1_2);
        return this;
    }

    public InstanceBuilder layers(String... layers) {
        this.layers = Sets.immutable.of(layers);
        return this;
    }

    public InstanceBuilder extensions(String... extensions) {
        this.extensions = Sets.immutable.of(extensions);
        return this;
    }

    public InstanceBuilder debugCallback(VkDebugUtilsMessengerCallbackEXTI debugCallback) {
        this.debugCallback = debugCallback;
        return this;
    }

    public VkInstance build() {
        try (MemoryStack stack = stackPush()) {
            VkInstanceCreateInfo instanceCreateInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(appInfo);

            if (layers != null && !layers.isEmpty()) {
                // Check layer availability
                IntBuffer layerCount = stack.ints(0);
                vkEnumerateInstanceLayerProperties(layerCount, null);

                VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);
                vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

                ImmutableSet<String> availableLayerNames = Sets.immutable.fromStream(
                        availableLayers.stream().map(VkLayerProperties::layerNameString));

                if (availableLayerNames.containsAllIterable(layers)) {
                    // Enable layers
                    PointerBuffer layerBuf = stack.mallocPointer(layers.size());
                    layers.forEach((x) -> layerBuf.put(stack.UTF8(x)));
                    instanceCreateInfo.ppEnabledLayerNames(layerBuf);
                } else {
                    throw new NotSupportedException("The following layers were requested during instance creation but are not supported: "
                            + layers.difference(availableLayerNames).makeString());
                }
            }
            if (extensions != null && !extensions.isEmpty()) {
                // Check extension availability
                IntBuffer extCount = stack.ints(0);
                vkEnumerateInstanceExtensionProperties((ByteBuffer) null, extCount, null);

                VkExtensionProperties.Buffer availableExts = VkExtensionProperties.malloc(extCount.get(0), stack);
                vkEnumerateInstanceExtensionProperties((ByteBuffer) null, extCount, availableExts);

                ImmutableSet<String> availableExtNames = Sets.immutable.fromStream(
                        availableExts.stream().map(VkExtensionProperties::extensionNameString));

                if (availableExtNames.containsAllIterable(extensions)) {
                    // Enable extensions
                    PointerBuffer extBuf = stack.mallocPointer(extensions.size());
                    extensions.forEach((x) -> extBuf.put(stack.UTF8(x)));
                    instanceCreateInfo.ppEnabledExtensionNames(extBuf);
                } else {
                    throw new NotSupportedException("The following extensions were requested during instance creation but are not supported: "
                            + extensions.difference(availableExtNames).makeString());
                }
            }
            if (debugCallback != null) {
                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);

                debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
                debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
                debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
                debugCreateInfo.pfnUserCallback(debugCallback);

                instanceCreateInfo.pNext(debugCreateInfo.address());
            }

            PointerBuffer instancePtr = stack.mallocPointer(1);
            if (vkCreateInstance(instanceCreateInfo, null, instancePtr) != VK_SUCCESS)
                throw new RuntimeException("Failed to create instance");

            // Cleanup
            if (appInfo != null) {
                memFree(appInfo.pApplicationName());
                memFree(appInfo.pEngineName());
                appInfo.free();
            }

            return new VkInstance(instancePtr.get(0), instanceCreateInfo);
        }
    }
}
