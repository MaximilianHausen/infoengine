package net.totodev.infoengine.core;

import net.totodev.infoengine.DisposedException;
import net.totodev.infoengine.IDisposable;
import net.totodev.infoengine.rendering.IRenderTarget;
import net.totodev.infoengine.rendering.opengl.Framebuffer;
import net.totodev.infoengine.rendering.opengl.enums.FramebufferBindTarget;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Represents a glfw window
 * @see <a href="https://www.glfw.org/docs/latest/window_guide.html">GLFW Docs: Windows</a>
 */
public class Window implements IDisposable, IRenderTarget {
    private static Window activeWindow;

    static {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Logger.log(LogLevel.Critical, "GLFW", "GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    private final long id;
    private boolean isDisposed;
    private WindowModes currentMode = WindowModes.Windowed;
    // Used to configure the window when exiting fullscreen
    private int lastWindowPosX = 0, lastWindowPosY = 0, lastWindowSizeX = 0, lastWindowSizeY = 0;

    /**
     * Creates a new window
     */
    public Window(@NotNull String title, int width, int height, boolean transparentFramebuffer) {
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, transparentFramebuffer ? GLFW_TRUE : GLFW_FALSE);
        id = glfwCreateWindow(width, height, title, NULL, activeWindow != null ? activeWindow.getId() : NULL);
        if (id == NULL)
            Logger.log(LogLevel.Critical, "GLFW", "Window could not be created");

        makeCurrent();
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(id);
        activeWindow = this;

        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " created and set as current");
    }

    public static Window getActiveWindow() {
        return activeWindow;
    }

    //region Random stuff
    /**
     * Processes all pending events. This should only be called from the main thread!
     */
    public static void pollEvents() {
        glfwPollEvents();
    }

    /**
     * Makes the OpenGL context not current on the calling thread.
     */
    public static void makeNotCurrent() {
        glfwMakeContextCurrent(NULL);
    }

    /**
     * Makes the OpenGL context of this window current on the calling thread.
     */
    public void makeCurrent() {
        if (isDisposed) throw new WindowDisposedException();
        glfwMakeContextCurrent(id);
        org.lwjgl.opengl.GL.createCapabilities();
    }

    public void setActive() {
        if (isDisposed) throw new WindowDisposedException();
        activeWindow = this;
    }

    public void requestAttention() {
        if (isDisposed) throw new WindowDisposedException();
        glfwRequestWindowAttention(id);
    }

    /**
     * Returns if the window should be closed (e.g. by clicking the close button).
     * @return If window should be closed
     */
    public boolean shouldClose() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwWindowShouldClose(id);
    }
    //endregion

    //region Minimize/Maximize
    public void minimize() {
        if (isDisposed) throw new WindowDisposedException();
        if (currentMode == WindowModes.Fullscreen) {
            Logger.log(LogLevel.Error, "GLFW", "Window " + id + " could not be minimized because it is in not in windowed mode");
            return;
        }
        glfwIconifyWindow(id);
        Logger.log(LogLevel.Error, "GLFW", "Window " + id + " minimized");
    }
    public boolean isMinimized() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_ICONIFIED) == GLFW_TRUE;
    }

    public void maximize() {
        if (isDisposed) throw new WindowDisposedException();
        if (currentMode == WindowModes.Fullscreen) {
            Logger.log(LogLevel.Error, "GLFW", "Window " + id + " could not be maximized because it is in not in windowed mode");
            return;
        }
        glfwMaximizeWindow(id);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " maximized");
    }
    public boolean isMaximized() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_MAXIMIZED) == GLFW_TRUE;
    }

    public void restore() {
        if (isDisposed) throw new WindowDisposedException();
        if (currentMode == WindowModes.Fullscreen) {
            Logger.log(LogLevel.Error, "GLFW", "Window " + id + " could not be restores because it is in not in windowed mode");
            return;
        }
        glfwRestoreWindow(id);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " restored");
    }
    //endregion

    //region Modes
    public void setMode(@NotNull WindowModes mode) {
        if (isDisposed) throw new WindowDisposedException();
        switch (mode) {
            case Windowed -> setWindowed();
            case Borderless -> Logger.log(LogLevel.Error, "GLFW", "Borderless mode is not supported yet");
            case Fullscreen -> setFullscreen();
        }
    }
    private void setWindowed() {
        switch (currentMode) {
            case Windowed -> Logger.log(LogLevel.Debug, "GLFW", "Window " + id + " already in windowed mode");
            case Fullscreen -> {
                glfwSetWindowMonitor(id, NULL, lastWindowPosX, lastWindowPosY, lastWindowSizeX, lastWindowSizeY, GLFW_DONT_CARE);
                glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                currentMode = WindowModes.Windowed;
                Logger.log(LogLevel.Info, "GLFW", "Window " + id + " set to windowed mode");
            }
        }
    }
    private void setFullscreen() {
        switch (currentMode) {
            case Windowed -> {
                int[] winPosX = new int[1], winPosY = new int[1], winSizeX = new int[1], winSizeY = new int[1];
                glfwGetWindowPos(id, winPosX, winPosY);
                glfwGetWindowSize(id, winSizeX, winSizeY);
                lastWindowPosX = winPosX[0];
                lastWindowPosY = winPosY[0];
                lastWindowSizeX = winSizeX[0];
                lastWindowSizeY = winSizeY[0];

                int[] monSizeX = new int[1], monSizeY = new int[1];
                glfwGetMonitorWorkarea(glfwGetWindowMonitor(id), new int[1], new int[1], monSizeX, monSizeY);

                glfwSetWindowMonitor(id, glfwGetWindowMonitor(id), 0, 0, monSizeX[0], monSizeY[0], GLFW_DONT_CARE);
                glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                currentMode = WindowModes.Fullscreen;
                Logger.log(LogLevel.Info, "GLFW", "Window " + id + " set to fullscreen mode");
            }
            case Fullscreen -> Logger.log(LogLevel.Debug, "GLFW", "Window " + id + " already in fullscreen mode");
        }
    }
    //endregion

    //region Pos/Size
    public void setPos(int x, int y) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowPos(id, x, y);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " moved to  {x=" + x + ", y=" + y + "}");
    }
    public Vector2i getPos() {
        if (isDisposed) throw new WindowDisposedException();
        int[] x = new int[1], y = new int[1];
        glfwGetWindowPos(id, x, y);
        return new Vector2i(x[0], y[0]);
    }
    public void setSize(int x, int y) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowSize(id, x, y);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " resized to  {x=" + x + ", y=" + y + "}");
    }
    public Vector2i getSize() {
        if (isDisposed) throw new WindowDisposedException();
        int[] x = new int[1], y = new int[1];
        glfwGetWindowSize(id, x, y);
        return new Vector2i(x[0], y[0]);
    }
    //endregion

    //region Get/Set random stuff
    /**
     * Use -1 to disable one value
     */
    public void setSizeLimit(int minX, int minY, int maxX, int maxY) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowSizeLimits(id, minX, minY, maxX, maxY);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " size limits set to {minX=" + minX + ", minY=" + minY + "}, {maxX=" + maxX + ", maxY=" + maxY + "}");
    }

    public void setAspectRatio(int x, int y) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowAspectRatio(id, x, y);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " aspect ratio set to {x=" + x + ", y=" + y + "}");
    }

    public void setTitle(String title) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowTitle(id, title);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " title set to \"" + title + "\"");
    }

    public boolean isFocused() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_FOCUSED) == GLFW_TRUE;
    }
    public void focus() {
        if (isDisposed) throw new WindowDisposedException();
        glfwFocusWindow(id);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " focused");
    }

    public Vector2i getFramebufferSize() {
        if (isDisposed) throw new WindowDisposedException();
        int[] x = new int[1], y = new int[1];
        glfwGetFramebufferSize(id, x, y);
        return new Vector2i(x[0], y[0]);
    }

    public boolean isFramebufferTransparent() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW_TRUE;
    }

    public Vector2f getContentScale() {
        if (isDisposed) throw new WindowDisposedException();
        float[] x = new float[1], y = new float[1];
        glfwGetWindowContentScale(id, x, y);
        return new Vector2f(x[0], y[0]);
    }

    public boolean isHovered() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_HOVERED) == GLFW_TRUE;
    }
    //endregion

    //region Attributes
    public boolean isVisible() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_VISIBLE) == GLFW_TRUE;
    }
    public void setVisible(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        if (bool) glfwShowWindow(id);
        else glfwHideWindow(id);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " set as  " + (bool ? "visible" : "hidden"));
    }

    public float getOpacity() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowOpacity(id);
    }
    public void setOpacity(float opacity) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowOpacity(id, opacity);
    }

    public boolean isResizable() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_RESIZABLE) == GLFW_TRUE;
    }
    public void setResizable(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowAttrib(id, GLFW_RESIZABLE, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " set as " + (bool ? "" : "not ") + "resizable");
    }

    public boolean isDecorated() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_DECORATED) == GLFW_TRUE;
    }
    public void setDecorated(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowAttrib(id, GLFW_DECORATED, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " set as " + (bool ? "" : "not ") + "decorated");
    }

    public boolean isFloating() {
        if (isDisposed) throw new WindowDisposedException();
        return glfwGetWindowAttrib(id, GLFW_FLOATING) == GLFW_TRUE;
    }
    public void setFloating(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowAttrib(id, GLFW_FLOATING, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogLevel.Info, "GLFW", "Window " + id + " set as " + (bool ? "" : "not ") + "floating");
    }
    //endregion

    public void setVsync(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSwapInterval(bool ? 1 : 0);
    }

    public long getId() {
        if (isDisposed) throw new WindowDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new WindowDisposedException();
        glfwDestroyWindow(id);
        isDisposed = true;
    }
    public boolean isDisposed() {
        return isDisposed;
    }

    public void activate() {
        Framebuffer fbo = Framebuffer.getBoundFramebuffer(FramebufferBindTarget.FRAMEBUFFER);
        if (fbo != null) fbo.unbind();
    }

    public void renderedFrame() {
        glfwSwapBuffers(id);
    }

    /**
     * This Exception is thrown when calling a method on a disposed window.
     */
    public static class WindowDisposedException extends DisposedException {
        public WindowDisposedException() {
            super("Window was already disposed");
        }
    }
}