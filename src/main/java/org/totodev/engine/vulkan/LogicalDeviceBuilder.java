package org.totodev.engine.vulkan;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.util.logging.*;

import java.util.function.Consumer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class LogicalDeviceBuilder {
    private VkPhysicalDevice physicalDevice;
    private Consumer<VkPhysicalDeviceFeatures> features = (features) -> {};
    private RichIterable<String> extensions = Lists.immutable.empty();
    private long pNext = VK_NULL_HANDLE;

    public LogicalDeviceBuilder physicalDevice(VkPhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;
        return this;
    }

    public LogicalDeviceBuilder features(Consumer<VkPhysicalDeviceFeatures> features) {
        this.features = features;
        return this;
    }

    public LogicalDeviceBuilder extensions(RichIterable<String> extensions) {
        this.extensions = extensions;
        return this;
    }

    public LogicalDeviceBuilder pNext(long pNext) {
        this.pNext = pNext;
        return this;
    }

    public VkDevice build() {
        try (MemoryStack stack = stackPush()) {
            QueueFamilies queueFamilies = new QueueFamilies(physicalDevice);

            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(queueFamilies.count(), stack);

            for (QueueFamily queueFamily : queueFamilies.all()) {
                VkDeviceQueueCreateInfo queueCreateInfo = queueCreateInfos.get(queueFamily.familyIndex());
                queueCreateInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
                queueCreateInfo.queueFamilyIndex(queueFamily.familyIndex());
                queueCreateInfo.pQueuePriorities(stack.floats(1.0f));
            }

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            features.accept(deviceFeatures);

            PointerBuffer pExtensions = stack.mallocPointer(extensions.size());
            extensions.forEach((x) -> pExtensions.put(stack.UTF8(x)));
            pExtensions.rewind();

            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(queueCreateInfos)
                    .pEnabledFeatures(deviceFeatures)
                    .ppEnabledExtensionNames(pExtensions)
                    .pNext(pNext);

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);
            if (vkCreateDevice(physicalDevice, createInfo, null, pDevice) != VK_SUCCESS)
                throw new RuntimeException("Failed to create logical device");

            Logger.log(LogLevel.TRACE, "VkBuilder", "Created logical device " + pDevice.get(0));
            return new VkDevice(pDevice.get(0), physicalDevice, createInfo);
        }
    }
}
