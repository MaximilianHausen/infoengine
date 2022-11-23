package org.totodev.engine.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetPhysicalDevicePresentationSupport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class QueueFamilies {
    private final QueueFamily[] families;

    public QueueFamilies(VkPhysicalDevice physicalDevice) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer familyProperties = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queueFamilyCount, familyProperties);

            families = new QueueFamily[familyProperties.capacity()];

            for (int i = 0; i < familyProperties.capacity(); i++) {
                var props = familyProperties.get(i);
                families[i] = new QueueFamily(physicalDevice, i, props.queueFlags(), props.queueCount());
            }
        }
    }

    public VkPhysicalDevice physicalDevice() {
        return families[0].device();
    }

    public QueueFamily[] all() {
        return families;
    }
    public int count() {
        return families.length;
    }

    public QueueFamily findQueueFamily(int flags, boolean presentSupport) {
        for (QueueFamily f : families)
            if (checkFamily(f, flags, presentSupport)) return f;
        return null;
    }

    public QueueFamily[] findQueueFamilies(int flags, boolean presentSupport) {
        return Arrays.stream(families).filter(f -> checkFamily(f, flags, presentSupport)).toArray(QueueFamily[]::new);
    }

    private boolean checkFamily(QueueFamily family, int flags, boolean presentSupport) {
        return (family.flags() & flags) == flags
            && (!presentSupport || glfwGetPhysicalDevicePresentationSupport(family.device().getInstance(), family.device(), family.familyIndex()));
    }
}
