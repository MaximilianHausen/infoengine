package org.totodev.engine.rendering.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;
import org.totodev.engine.core.Engine;

import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public final class VkSurfaceHelper {
    public static long createSurface(long glfwWindow) {
        return createSurface(Engine.getVkInstance(), glfwWindow);
    }
    public static long createSurface(VkInstance instance, long glfwWindow) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);

            if (glfwCreateWindowSurface(instance, glfwWindow, null, pSurface) != VK_SUCCESS)
                throw new RuntimeException("Failed to create window surface");

            return pSurface.get(0);
        }
    }
}
