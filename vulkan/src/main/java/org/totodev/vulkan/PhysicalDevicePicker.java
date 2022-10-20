package org.totodev.vulkan;

import org.eclipse.collections.api.factory.*;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.function.Predicate;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class PhysicalDevicePicker {
    private VkInstance instance;
    private Predicate<QueueFamilies> queuePredicate = (queueFamilies) -> true;
    private Predicate<VkPhysicalDeviceFeatures> featurePredicate = (features) -> true;
    private Iterable<String> requiredExtensions = Lists.immutable.empty();

    public PhysicalDevicePicker instance(VkInstance instance) {
        this.instance = instance;
        return this;
    }

    public PhysicalDevicePicker queueFamilies(Predicate<QueueFamilies> queuePredicate) {
        this.queuePredicate = queuePredicate;
        return this;
    }

    public PhysicalDevicePicker features(Predicate<VkPhysicalDeviceFeatures> featurePredicate) {
        this.featurePredicate = featurePredicate;
        return this;
    }

    public PhysicalDevicePicker extensions(Iterable<String> requiredExtensions) {
        this.requiredExtensions = requiredExtensions;
        return this;
    }

    public @Nullable VkPhysicalDevice pick() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, deviceCount, null);

            if (deviceCount.get(0) == 0)
                return null;

            PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, ppPhysicalDevices);

            for (int i = 0; i < ppPhysicalDevices.capacity(); i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(ppPhysicalDevices.get(i), instance);

                if (!queuePredicate.test(new QueueFamilies(device))) continue;

                VkPhysicalDeviceFeatures supportedFeatures = VkPhysicalDeviceFeatures.calloc(stack);
                vkGetPhysicalDeviceFeatures(device, supportedFeatures);
                if (!featurePredicate.test(supportedFeatures)) continue;

                if (!checkExtensionSupport(device, Sets.mutable.ofAll(requiredExtensions))) continue;

                //TODO: Swapchain support checking
                //VkSwapchainHelper.SwapchainSupportDetails swapChainSupport = VkSwapchainHelper.querySwapChainSupport(device, surface);
                //if (!swapChainSupport.formats().hasRemaining() || swapChainSupport.presentModes().hasRemaining()) continue;

                return device;
            }

            return null;
        }
    }

    // requiredExtensions will contain all extensions that are not supported
    private static boolean checkExtensionSupport(VkPhysicalDevice device, MutableSet<String> requiredExtensions) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer extensionCount = stack.ints(0);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, null);

            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.malloc(extensionCount.get(0), stack);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, availableExtensions);

            return requiredExtensions.isEmpty() || availableExtensions.stream()
                    .map(VkExtensionProperties::extensionNameString)
                    .anyMatch(s -> requiredExtensions.remove(s) && requiredExtensions.isEmpty());
        }
    }
}
