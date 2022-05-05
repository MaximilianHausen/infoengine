package net.totodev.infoengine.core;

import net.totodev.infoengine.ecs.Scene;
import net.totodev.infoengine.rendering.vulkan.*;
import net.totodev.infoengine.util.SemVer;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.*;
import org.eclipse.collections.api.set.ImmutableSet;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.vulkan.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class Engine {
    public static final ImmutableSet<String> VULKAN_EXTENSIONS = Sets.immutable.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    public static final ImmutableSet<String> VALIDATION_LAYERS = Sets.immutable.of("VK_LAYER_KHRONOS_validation");

    private static Window mainWindow;
    private static String appName;

    private static VkInstance vkInstance;
    private static long vkDebugManager;
    private static VkPhysicalDevice vkPhysicalDevice;
    private static VkDevice vkLogicalDevice;

    private static VkQueue graphicsQueue;
    private static VkQueue presentQueue;

    public static void initialize(String appName, SemVer appVersion) {
        initGlfw();

        vkInstance = VkInstanceHelper.createInstance(appName, appVersion, VALIDATION_LAYERS);
        vkDebugManager = VkDebugUtilsHelper.createDebugMessenger(vkInstance);
        Engine.appName = appName;
    }

    /**
     * Creates a new window and starts
     */
    public static void start(Scene startScene) {
        mainWindow = new MainWindow(appName, 800, 600, false);

        mainWindow.setVisible(true);
    }

    private static void cleanup() {
        if (vkDebugManager != 0 && vkGetInstanceProcAddr(vkInstance, "vkDestroyDebugUtilsMessengerEXT") != NULL)
            vkDestroyDebugUtilsMessengerEXT(vkInstance, vkDebugManager, null);
        vkDestroyInstance(vkInstance, null);
        glfwTerminate();
    }

    private static void initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            Logger.log(LogLevel.Critical, "GLFW", "GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    public static VkInstance getVkInstance() {
        return vkInstance;
    }
    public static Window getMainWindow() {
        return mainWindow;
    }

    public static VkPhysicalDevice getPhysicalDevice() {
        return vkPhysicalDevice;
    }
    static void setPhysicalDevice(VkPhysicalDevice physicalDevice) {
        Engine.vkPhysicalDevice = physicalDevice;
    }
    public static VkDevice getLogicalDevice() {
        return vkLogicalDevice;
    }
    static void setLogicalDevice(VkDevice logicalDevice) {
        Engine.vkLogicalDevice = logicalDevice;
    }

    public static VkQueue getGraphicsQueue() {
        return graphicsQueue;
    }
    static void setGraphicsQueue(VkQueue graphicsQueue) {
        Engine.graphicsQueue = graphicsQueue;
    }
    public static VkQueue getPresentQueue() {
        return presentQueue;
    }
    static void setPresentQueue(VkQueue presentQueue) {
        Engine.presentQueue = presentQueue;
    }
}
