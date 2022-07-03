package net.totodev.infoengine.core;

import net.totodev.infoengine.rendering.vulkan.*;
import net.totodev.infoengine.util.SemVer;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.vulkan.*;

import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class Engine {
    public record WorkerResources(long commandPool) {
    }

    public static final ImmutableSet<String> VULKAN_EXTENSIONS = Sets.immutable.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    public static final ImmutableSet<String> VALIDATION_LAYERS = Sets.immutable.of("VK_LAYER_KHRONOS_validation");

    private static Window mainWindow;

    //region Vulkan
    private static VkInstance vkInstance;
    private static long vkDebugManager;
    private static VkPhysicalDevice vkPhysicalDevice;
    private static VkDevice vkLogicalDevice;

    private static int graphicsQueueFamily;
    private static int presentQueueFamily;
    private static VkQueue graphicsQueue;
    private static VkQueue presentQueue;
    //endregion

    //region Threading
    private static final Thread mainThread = Thread.currentThread();
    private static final BlockingDeque<Runnable> mainThreadQueue = new LinkedBlockingDeque<>();

    private static final ThreadPoolExecutor workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    private static final ThreadLocal<WorkerResources> workerResources = ThreadLocal.withInitial(() -> new WorkerResources(
            VkCommandBufferHelper.createCommandPool(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT, getGraphicsQueueFamily())
    ));
    //endregion

    /**
     * Initializes glfw and vulkan and creates a hidden main window.
     * This should be called from the main thread as soon as possible.
     * @param appName      The name of this application, used to init vulkan and name the main window
     * @param appVersion   The version of this application, used to init vulkan
     * @param windowWidth  The width of the main window in screen coordinates
     * @param windowHeight The height of the main window in screen coordinates
     */
    public static void initialize(String appName, SemVer appVersion, int windowWidth, int windowHeight) {
        initGlfw();

        vkInstance = VkInstanceHelper.createInstance(appName, appVersion, VALIDATION_LAYERS);
        vkDebugManager = VkDebugUtilsHelper.createDebugMessenger(vkInstance);
        mainWindow = new MainWindow(appName, windowWidth, windowHeight, true);
    }

    /**
     * Locks this thread until the main window is closed or {@link Engine#terminate()} is called from another thread.
     * While locked, code can be executed on this thread through {@link Engine#executeOnMainThread(Runnable)}. <br/>
     * Must be called from the main thread.
     */
    public static void start() {
        glfwSetWindowCloseCallback(mainWindow.getId(), windowId -> terminate());

        mainWindow.setVisible(true);

        while (true) {
            try {
                mainThreadQueue.take().run();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void terminate() {
        mainThread.interrupt();
        cleanup();
    }

    /**
     * Runs a task asynchronously on the main thread.
     * The main thread must be locked in {@link Engine#start()}.
     * @param task The task to run
     */
    public static void executeOnMainThread(Runnable task) {
        mainThreadQueue.add(task);
    }

    /**
     * Runs a task asynchronously on the worker thread pool.
     * @param task The task to run
     */
    public static void executeOnWorkerPool(Consumer<WorkerResources> task) {
        workers.execute(() -> task.accept(workerResources.get()));
    }

    private static void initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new RuntimeException("GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    private static void cleanup() {
        mainWindow.close();
        vkDestroyDevice(vkLogicalDevice, null);
        if (vkDebugManager != 0 && vkGetInstanceProcAddr(vkInstance, "vkDestroyDebugUtilsMessengerEXT") != NULL)
            vkDestroyDebugUtilsMessengerEXT(vkInstance, vkDebugManager, null);
        vkDestroyInstance(vkInstance, null);
        glfwTerminate();
    }

    //region Properties
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

    public static int getGraphicsQueueFamily() {
        return graphicsQueueFamily;
    }
    static void setGraphicsQueueFamily(int graphicsQueueFamily) {
        Engine.graphicsQueueFamily = graphicsQueueFamily;
    }

    public static int getPresentQueueFamily() {
        return presentQueueFamily;
    }
    static void setPresentQueueFamily(int presentQueueFamily) {
        Engine.presentQueueFamily = presentQueueFamily;
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
    //endregion
}
