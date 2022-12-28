package org.totodev.engine.core;

import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;
import org.totodev.engine.rendering.VkBuilder;
import org.totodev.engine.rendering.vulkan.*;
import org.totodev.engine.util.logging.*;
import org.totodev.engine.vulkan.SwapchainBuilder;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Represents a glfw window
 * @see <a href="https://www.glfw.org/docs/latest/window_guide.html">GLFW Docs: Windows</a>
 */
public class Window implements AutoCloseable {
    private final long id;
    private boolean closed = false;
    private WindowModes currentMode = WindowModes.WINDOWED;
    // Used to configure the window when exiting fullscreen
    private int lastWindowPosX = 0, lastWindowPosY = 0, lastWindowSizeX = 0, lastWindowSizeY = 0;

    //region Vulkan
    private final long vkSurface;

    private final long vkSwapchain;
    private final int vkImageFormat;
    private final VkExtent2D vkExtent;

    private final LongList vkImages;
    private final LongList vkImageViews;
    //endregion

    /**
     * Creates a new window
     */
    public Window(@NotNull String title, int width, int height, boolean startHidden) {
        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL)
            Logger.log(LogLevel.CRITICAL, "GLFW", "Window could not be created");

        try (MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if (glfwCreateWindowSurface(Engine.getVkInstance(), id, null, pSurface) != VK_SUCCESS)
                throw new RuntimeException("Failed to create window surface");
            vkSurface = pSurface.get(0);
        }

        SwapchainBuilder.SwapchainCreationResult swapchainCreationResult = VkBuilder.swapchain(this)
            .imageCount(3)
            .queues(Engine.getGraphicsQueueFamily(), Engine.getPresentQueueFamily())
            .build();
        vkSwapchain = swapchainCreationResult.swapchain();
        vkImages = swapchainCreationResult.images();
        vkImageFormat = swapchainCreationResult.imageFormat();
        vkExtent = swapchainCreationResult.extent();

        vkImageViews = LongLists.immutable.of(VkBuilder.imageView().images(vkImages.toArray()).imageFormat(vkImageFormat).buildAll());

        if (!startHidden) glfwShowWindow(id);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " created and set as current");
    }

    //region Random stuff
    /**
     * Processes all pending events. This must be called from the main thread!
     */
    public static void pollEvents() {
        glfwPollEvents();
    }

    public void requestAttention() {
        if (closed) throw new WindowClosedException();
        glfwRequestWindowAttention(id);
    }

    /**
     * Returns whether the window should be closed (e.g. by clicking the close button).
     * @return Whether window should be closed
     */
    public boolean shouldClose() {
        if (closed) throw new WindowClosedException();
        return glfwWindowShouldClose(id);
    }
    //endregion

    //region Minimize/Maximize
    public void minimize() {
        if (closed) throw new WindowClosedException();
        if (currentMode == WindowModes.FULLSCREEN) {
            Logger.log(LogLevel.ERROR, "GLFW", "Window " + id + " could not be minimized because it is in not in windowed mode");
            return;
        }
        glfwIconifyWindow(id);
        Logger.log(LogLevel.ERROR, "GLFW", "Window " + id + " minimized");
    }
    public boolean isMinimized() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_ICONIFIED) == GLFW_TRUE;
    }

    public void maximize() {
        if (closed) throw new WindowClosedException();
        if (currentMode == WindowModes.FULLSCREEN) {
            Logger.log(LogLevel.ERROR, "GLFW", "Window " + id + " could not be maximized because it is in not in windowed mode");
            return;
        }
        glfwMaximizeWindow(id);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " maximized");
    }
    public boolean isMaximized() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_MAXIMIZED) == GLFW_TRUE;
    }

    public void restore() {
        if (closed) throw new WindowClosedException();
        if (currentMode == WindowModes.FULLSCREEN) {
            Logger.log(LogLevel.ERROR, "GLFW", "Window " + id + " could not be restores because it is in not in windowed mode");
            return;
        }
        glfwRestoreWindow(id);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " restored");
    }
    //endregion

    //region Modes
    public void setMode(@NotNull WindowModes mode) {
        if (closed) throw new WindowClosedException();
        switch (mode) {
            case WINDOWED -> setWindowed();
            case BORDERLESS -> Logger.log(LogLevel.ERROR, "GLFW", "Borderless mode is not supported yet");
            case FULLSCREEN -> setFullscreen();
        }
    }
    private void setWindowed() {
        switch (currentMode) {
            case WINDOWED -> Logger.log(LogLevel.DEBUG, "GLFW", "Window " + id + " already in windowed mode");
            case FULLSCREEN -> {
                glfwSetWindowMonitor(id, NULL, lastWindowPosX, lastWindowPosY, lastWindowSizeX, lastWindowSizeY, GLFW_DONT_CARE);
                glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                currentMode = WindowModes.WINDOWED;
                Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " set to windowed mode");
            }
        }
    }
    private void setFullscreen() {
        switch (currentMode) {
            case WINDOWED -> {
                try (MemoryStack stack = stackPush()) {
                    IntBuffer winPosX = stack.callocInt(1), winPosY = stack.callocInt(1), winSizeX = stack.callocInt(1), winSizeY = stack.callocInt(1);
                    glfwGetWindowPos(id, winPosX, winPosY);
                    glfwGetWindowSize(id, winSizeX, winSizeY);
                    lastWindowPosX = winPosX.get();
                    lastWindowPosY = winPosY.get();
                    lastWindowSizeX = winSizeX.get();
                    lastWindowSizeY = winSizeY.get();

                    IntBuffer monSizeX = winSizeX, monSizeY = winSizeY; // Reusing the old buffers. Reassigning for readability.
                    glfwGetMonitorWorkarea(glfwGetWindowMonitor(id), null, null, monSizeX, monSizeY);

                    glfwSetWindowMonitor(id, glfwGetWindowMonitor(id), 0, 0, monSizeX.get(), monSizeY.get(), GLFW_DONT_CARE);
                    glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                    currentMode = WindowModes.FULLSCREEN;
                    Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " set to fullscreen mode");
                }
            }
            case FULLSCREEN -> Logger.log(LogLevel.DEBUG, "GLFW", "Window " + id + " already in fullscreen mode");
        }
    }
    //endregion

    //region Pos/Size
    public void setPos(int x, int y) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowPos(id, x, y);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " moved to  {x=" + x + ", y=" + y + "}");
    }
    public Vector2i getPos() {
        if (closed) throw new WindowClosedException();
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            glfwGetWindowPos(id, x, y);
            return new Vector2i(x.get(), y.get());
        }
    }
    public void setSize(int x, int y) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowSize(id, x, y);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " resized to  {x=" + x + ", y=" + y + "}");
    }
    public Vector2i getSize() {
        if (closed) throw new WindowClosedException();
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            glfwGetWindowSize(id, x, y);
            return new Vector2i(x.get(), y.get());
        }
    }
    public int getWidth() {
        if (closed) throw new WindowClosedException();
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            glfwGetWindowSize(id, x, null);
            return x.get();
        }
    }
    public int getHeight() {
        if (closed) throw new WindowClosedException();
        try (MemoryStack stack = stackPush()) {
            IntBuffer y = stack.mallocInt(1);
            glfwGetWindowSize(id, null, y);
            return y.get();
        }
    }
    //endregion

    //region Get/Set random stuff
    /**
     * Use -1 to disable one value
     */
    public void setSizeLimit(int minX, int minY, int maxX, int maxY) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowSizeLimits(id, minX, minY, maxX, maxY);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " size limits set to {minX=" + minX + ", minY=" + minY + "}, {maxX=" + maxX + ", maxY=" + maxY + "}");
    }

    public void setAspectRatio(int x, int y) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowAspectRatio(id, x, y);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " aspect ratio set to {x=" + x + ", y=" + y + "}");
    }

    public void setTitle(String title) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowTitle(id, title);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " title set to \"" + title + "\"");
    }

    public boolean isFocused() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_FOCUSED) == GLFW_TRUE;
    }
    public void focus() {
        if (closed) throw new WindowClosedException();
        glfwFocusWindow(id);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " focused");
    }

    public Vector2i getFramebufferSize() {
        if (closed) throw new WindowClosedException();
        try (MemoryStack stack = stackPush()) {
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1);
            glfwGetFramebufferSize(id, x, y);
            return new Vector2i(x.get(), y.get());
        }
    }

    public Vector2f getContentScale() {
        if (closed) throw new WindowClosedException();
        try (MemoryStack stack = stackPush()) {
            FloatBuffer x = stack.callocFloat(1), y = stack.callocFloat(1);
            glfwGetWindowContentScale(id, x, y);
            return new Vector2f(x.get(), y.get());
        }
    }

    public boolean isHovered() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_HOVERED) == GLFW_TRUE;
    }
    //endregion

    //region Attributes
    public boolean isVisible() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_VISIBLE) == GLFW_TRUE;
    }
    public void setVisible(boolean bool) {
        if (closed) throw new WindowClosedException();
        if (bool) glfwShowWindow(id);
        else glfwHideWindow(id);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " set as " + (bool ? "visible" : "hidden"));
    }

    public float getOpacity() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowOpacity(id);
    }
    public void setOpacity(float opacity) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowOpacity(id, opacity);
    }

    public boolean isResizable() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_RESIZABLE) == GLFW_TRUE;
    }
    public void setResizable(boolean bool) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowAttrib(id, GLFW_RESIZABLE, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " set as " + (bool ? "" : "not ") + "resizable");
    }

    public boolean isDecorated() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_DECORATED) == GLFW_TRUE;
    }
    public void setDecorated(boolean bool) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowAttrib(id, GLFW_DECORATED, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " set as " + (bool ? "" : "not ") + "decorated");
    }

    public boolean isFloating() {
        if (closed) throw new WindowClosedException();
        return glfwGetWindowAttrib(id, GLFW_FLOATING) == GLFW_TRUE;
    }
    public void setFloating(boolean bool) {
        if (closed) throw new WindowClosedException();
        glfwSetWindowAttrib(id, GLFW_FLOATING, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogLevel.INFO, "GLFW", "Window " + id + " set as " + (bool ? "" : "not ") + "floating");
    }
    //endregion

    //region Vulkan Get
    public long getVkSurface() {
        return vkSurface;
    }
    public long getVkSwapchain() {
        return vkSwapchain;
    }
    public LongList getVkImages() {
        return vkImages;
    }
    public LongList getVkImageViews() {
        return vkImageViews;
    }
    public int getVkImageFormat() {
        return vkImageFormat;
    }
    public VkExtent2D getVkExtent() {
        return vkExtent;
    }
    //endregion

    public long getId() {
        if (closed) throw new WindowClosedException();
        return id;
    }

    @Override
    public void close() {
        if (closed) return;
        vkImageViews.forEach(imageView -> vkDestroyImageView(Engine.getLogicalDevice(), imageView, null));
        vkExtent.free();
        vkDestroySwapchainKHR(Engine.getLogicalDevice(), vkSwapchain, null);
        glfwDestroyWindow(id);
        closed = true;
    }

    /**
     * This Exception is thrown when calling a method on a previously closed window.
     */
    public static class WindowClosedException extends RuntimeException {
        public WindowClosedException() {
            super("Window was already closed");
        }
    }
}
