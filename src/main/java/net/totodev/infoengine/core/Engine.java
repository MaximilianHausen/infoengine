package net.totodev.infoengine.core;

import net.totodev.infoengine.rendering.*;
import net.totodev.infoengine.util.SemVer;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.vulkan.VkInstance;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
    private static Window mainWindow;

    private static VkInstance vkInstance;
    private static long debugManager;

    public static void initialize(String appName, SemVer appVersion) {
        initGlfw();

        ImmutableSet<String> validationLayers = Sets.immutable.of("VK_LAYER_KHRONOS_validation");
        vkInstance = VkInstanceHelper.createInstance(appName, appVersion, validationLayers);
        debugManager = VkDebugUtilsHelper.createDebugMessenger(vkInstance);
    }

    private static void initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Logger.log(LogLevel.Critical, "GLFW", "GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }
}
