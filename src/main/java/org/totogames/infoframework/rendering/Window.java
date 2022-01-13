package org.totogames.infoframework.rendering;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.totogames.infoframework.util.logging.LogSeverity;
import org.totogames.infoframework.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    static {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Logger.log(LogSeverity.Critical, "Window", "GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    }

    public enum WindowModes {
        Windowed, Borderless, Fullscreen
    }

    private final long id;
    private WindowModes currentMode = WindowModes.Windowed;

    public Window(String title, int width, int height) {
        id = glfwCreateWindow(width, height, title, NULL, NULL /*TODO: Whats that*/);
        if (id == NULL)
            Logger.log(LogSeverity.Critical, "Window", "Window could not be created");

        glfwMakeContextCurrent(id);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(id);
        org.lwjgl.opengl.GL.createCapabilities();
    }

    public long getWindow() {
        return id;
    }

    public void setMode(WindowModes mode) {
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
                glfwSetWindowMonitor(id, NULL, 0, 0, 500, 500, GLFW_DONT_CARE);
                glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                currentMode = WindowModes.Windowed;
                Logger.log(LogSeverity.Info, "Window", "Window " + id + " set to windowed mode");
            }
        }
    }

    private void setFullscreen() {
        switch (currentMode) {
            case Windowed -> {
                glfwSetWindowMonitor(id, glfwGetPrimaryMonitor(), 0, 0, 1920, 1080, GLFW_DONT_CARE);
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
}
