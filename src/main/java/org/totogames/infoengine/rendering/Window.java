package org.totogames.infoengine.rendering;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.totogames.infoengine.IDisposable;
import org.totogames.infoengine.rendering.opengl.wrappers.Framebuffer;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements IDisposable, IRenderTarget {
    private static Window activeWindow;

    static {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Logger.log(LogSeverity.Critical, "Window", "GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    }

    private final long id;
    private boolean isDisposed;
    private WindowModes currentMode = WindowModes.Windowed;
    // Used to configure the window when exiting fullscreen
    private int lastWindowPosX = 0, lastWindowPosY = 0, lastWindowSizeX = 0, lastWindowSizeY = 0;

    public Window(@NotNull String title, int width, int height) {
        id = glfwCreateWindow(width, height, title, NULL, activeWindow != null ? activeWindow.getId() : NULL);
        if (id == NULL)
            Logger.log(LogSeverity.Critical, "GLFW", "Window could not be created");

        makeCurrent();
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(id);
        activeWindow = this;

        Logger.log(LogSeverity.Debug, "GLFW", "Window " + id + " created and set as current");
    }

    public static Window getActiveWindow() {
        return activeWindow;
    }
    public static void makeNotCurrent() {
        glfwMakeContextCurrent(NULL);
    }
    public static void pollEvents() {
        glfwPollEvents();
    }

    public long getId() {
        if (isDisposed) throw new WindowDisposedException();
        return id;
    }
    public void makeCurrent() {
        if (isDisposed) throw new WindowDisposedException();
        glfwMakeContextCurrent(id);
        org.lwjgl.opengl.GL.createCapabilities();
    }
    public void setActive() {
        if (isDisposed) throw new WindowDisposedException();
        activeWindow = this;
    }

    public void setMode(@NotNull WindowModes mode) {
        if (isDisposed) throw new WindowDisposedException();
        switch (mode) {
            case Windowed -> setWindowed();
            case Borderless -> Logger.log(LogSeverity.Error, "Window", "Borderless mode is not supported yet");
            case Fullscreen -> setFullscreen();
        }
    }

    private void setWindowed() {
        switch (currentMode) {
            case Windowed -> Logger.log(LogSeverity.Debug, "Window", "Window " + id + " already in windowed mode");
            case Fullscreen -> {
                glfwSetWindowMonitor(id, NULL, lastWindowPosX, lastWindowPosY, lastWindowSizeX, lastWindowSizeY, GLFW_DONT_CARE);
                glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                currentMode = WindowModes.Windowed;
                Logger.log(LogSeverity.Info, "Window", "Window " + id + " set to windowed mode");
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
                Logger.log(LogSeverity.Info, "Window", "Window " + id + " set to fullscreen mode");
            }
            case Fullscreen -> Logger.log(LogSeverity.Debug, "Window", "Window " + id + " already in fullscreen mode");
        }
    }

    public void setPos(int x, int y) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowPos(id, x, y);
    }

    public void setSize(int x, int y) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSetWindowSize(id, x, y);
    }

    public void setVisible(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        if ((bool ? GLFW_TRUE : GLFW_FALSE) == glfwGetWindowAttrib(id, GLFW_VISIBLE)) {
            Logger.log(LogSeverity.Debug, "WindowManager", "Window " + id + " already " + (bool ? "visible" : "hidden"));
            return;
        }

        if (bool) glfwShowWindow(id);
        else glfwHideWindow(id);
        Logger.log(LogSeverity.Info, "Window", "Window " + id + " set as  " + (bool ? "visible" : "hidden"));
    }

    public void setResizable(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        if ((bool ? GLFW_TRUE : GLFW_FALSE) == glfwGetWindowAttrib(id, GLFW_VISIBLE)) {
            Logger.log(LogSeverity.Debug, "Window", "Window " + id + " already " + (bool ? "" : "not ") + "resizable");
            return;
        }

        glfwSetWindowAttrib(id, GLFW_RESIZABLE, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogSeverity.Info, "Window", "Window " + id + " set as " + (bool ? "" : "not ") + "resizable");
    }

    public void setVsync(boolean bool) {
        if (isDisposed) throw new WindowDisposedException();
        glfwSwapInterval(bool ? 1 : 0);
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
        Framebuffer.unbind();
    }
    public void renderedFrame() {
        glfwSwapBuffers(id);
    }
}
