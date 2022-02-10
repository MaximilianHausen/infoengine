package org.totogames.infoengine.rendering;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.totogames.infoengine.rendering.opengl.wrappers.Framebuffer;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements IRenderTarget {
    static {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Logger.log(LogSeverity.Critical, "Window", "GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    }

    private final long id;
    private WindowModes currentMode = WindowModes.Windowed;
    // Used to configure the window when exiting fullscreen
    private int lastWindowPosX = 0, lastWindowPosY = 0, lastWindowSizeX = 0, lastWindowSizeY = 0;

    public Window(@NotNull String title, int width, int height) {
        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL)
            Logger.log(LogSeverity.Critical, "Window", "Window could not be created");

        glfwMakeContextCurrent(id);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(id);
        org.lwjgl.opengl.GL.createCapabilities();
    }

    public long getId() {
        return id;
    }

    public void makeCurrent() {
        glfwMakeContextCurrent(id);
    }

    public void setMode(@NotNull WindowModes mode) {
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
        glfwSetWindowPos(id, x, y);
    }

    public void setSize(int x, int y) {
        glfwSetWindowSize(id, x, y);
    }

    public void setVisible(boolean bool) {
        if ((bool ? GLFW_TRUE : GLFW_FALSE) == glfwGetWindowAttrib(id, GLFW_VISIBLE)) {
            Logger.log(LogSeverity.Debug, "WindowManager", "Window " + id + " already " + (bool ? "visible" : "hidden"));
            return;
        }

        if (bool) glfwShowWindow(id);
        else glfwHideWindow(id);
        Logger.log(LogSeverity.Info, "Window", "Window " + id + " set as  " + (bool ? "visible" : "hidden"));
    }

    public void setResizable(boolean bool) {
        if ((bool ? GLFW_TRUE : GLFW_FALSE) == glfwGetWindowAttrib(id, GLFW_VISIBLE)) {
            Logger.log(LogSeverity.Debug, "Window", "Window " + id + " already " + (bool ? "" : "not ") + "resizable");
            return;
        }

        glfwSetWindowAttrib(id, GLFW_RESIZABLE, bool ? GLFW_TRUE : GLFW_FALSE);
        Logger.log(LogSeverity.Info, "Window", "Window " + id + " set as " + (bool ? "" : "not ") + "resizable");
    }

    public void setVsync(boolean bool) {
        glfwSwapInterval(bool ? 1 : 0);
    }


    public void activate() {
        Framebuffer.unbind();
    }
    public void renderedFrame() {
        glfwSwapBuffers(id);
        glfwPollEvents();
    }
}
