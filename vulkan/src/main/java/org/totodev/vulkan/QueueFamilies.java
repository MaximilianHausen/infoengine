package org.totodev.vulkan;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;

public class QueueFamilies {
    public record QueueFamily(VkPhysicalDevice device, int index, int flags, int queueCount) {
        //TODO: Queue Creation
        /*PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);
        vkGetDeviceQueue(device, queueFamilies.findQueueFamily(VK_QUEUE_GRAPHICS_BIT, null).index(), 0, pQueue);
        VkQueue graphicsQueue = new VkQueue(pQueue.get(0), device);
        vkGetDeviceQueue(device, queueFamilies.findQueueFamily(0, surface).index(), 0, pQueue);
        VkQueue presentQueue = new VkQueue(pQueue.get(0), device);*/
    }

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

    public QueueFamily findQueueFamily(int flags, @Nullable Long presentSurface) {
        for (QueueFamily f : families)
            if (checkFamily(f, flags, presentSurface)) return f;
        return null;
    }

    public QueueFamily[] findQueueFamilies(int flags, @Nullable Long presentSurface) {
        return Arrays.stream(families).filter(f -> checkFamily(f, flags, presentSurface)).toArray(QueueFamily[]::new);
    }

    private boolean checkFamily(QueueFamily family, int flags, @Nullable Long presentSurface) {
        if ((family.flags() & flags) == 0) return false;
        if (presentSurface == null) return true;
        try (MemoryStack stack = stackPush()) {
            IntBuffer presentSupport = stack.ints(VK_FALSE);
            vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice(), family.index(), presentSurface, presentSupport);
            if (presentSupport.get(0) == VK_TRUE) return true;
        }
        return false;
    }
}
