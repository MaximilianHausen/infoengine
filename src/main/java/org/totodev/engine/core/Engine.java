package org.totodev.engine.core;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.totodev.engine.rendering.VkBuilder;
import org.totodev.engine.rendering.vulkan.VkCommandBufferHelper;
import org.totodev.engine.util.logging.*;
import org.totodev.engine.vulkan.*;

import java.util.concurrent.*;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

public class Engine {
    public record WorkerResources(long commandPool) {
    }

    private static String appName;
    private static SemVer appVersion;

    private static Window mainWindow;

    //region Vulkan
    private static VkInstance vkInstance;
    private static long vkDebugManager;
    private static VkPhysicalDevice vkPhysicalDevice;
    private static VkDevice vkLogicalDevice;

    //TODO: Rework queue management
    private static QueueFamily graphicsQueueFamily;
    private static QueueFamily presentQueueFamily;
    private static VkQueue graphicsQueue;
    private static VkQueue presentQueue;
    //endregion

    //region Threading
    private static final BlockingDeque<Runnable> mainThreadQueue = new LinkedBlockingDeque<>();

    private static final ThreadPoolExecutor workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    private static final ThreadLocal<WorkerResources> workerResources = ThreadLocal.withInitial(() -> new WorkerResources(
        VkCommandBufferHelper.createCommandPool(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT, getGraphicsQueueFamily().familyIndex())
    ));
    //endregion

    /**
     * Initializes glfw and vulkan. This should be called from the main thread as soon as possible.
     * @param appName    The name of this application, used to init vulkan and name the main window in {@link Engine#start(int, int)}
     * @param appVersion The version of this application, used to init vulkan
     */
    public static void initialize(String appName, SemVer appVersion) {
        Engine.appName = appName;
        Engine.appVersion = appVersion;

        initGlfw();
        initVulkan();

        Logger.log(LogLevel.DEBUG, "Engine", "Initialized");
    }

    private static void initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new RuntimeException("GLFW could not be initialized");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    private static void initVulkan() {
        PointerBuffer ppGlfwInstanceExtensions = glfwGetRequiredInstanceExtensions();
        if (ppGlfwInstanceExtensions == null) throw new RuntimeException("Vulkan instance extensions required for window creation not found");
        MutableSet<String> instanceExtensions = Sets.mutable.empty();
        while (ppGlfwInstanceExtensions.hasRemaining())
            instanceExtensions.add(ppGlfwInstanceExtensions.getStringASCII());

        ImmutableSet<String> deviceExtensions = Sets.immutable.of(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME);

        vkInstance = VkBuilder.instance()
            .appInfo(appName, appVersion, "infoengine", new SemVer(1, 0, 0))
            .layers(Sets.immutable.of("VK_LAYER_KHRONOS_validation"))
            .extensions(instanceExtensions)
            .debugCallback(Logger::vkLoggingCallback)
            .build();

        vkDebugManager = VkBuilder.debugUtilsMessenger()
            .callback(Logger::vkLoggingCallback)
            .build();

        vkPhysicalDevice = VkBuilder.physicalDevice()
            .extensions(deviceExtensions)
            .features((features) -> true)
            .queueFamilies((families) -> families.findQueueFamily(VK_QUEUE_GRAPHICS_BIT, false) != null && families.findQueueFamily(0, true) != null)
            .pick();

        try (MemoryStack stack = stackPush()) {
            VkPhysicalDeviceDescriptorIndexingFeaturesEXT indexingFeatures = VkPhysicalDeviceDescriptorIndexingFeaturesEXT.calloc(stack)
                .sType(VK12.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES)
                .runtimeDescriptorArray(true)
                .descriptorBindingPartiallyBound(true);

            vkLogicalDevice = VkBuilder.logicalDevice()
                .physicalDevice(Engine.getPhysicalDevice())
                .extensions(deviceExtensions)
                .features(features -> features.samplerAnisotropy(true))
                .pNext(indexingFeatures.address())
                .build();
        }

        QueueFamilies queueFamilies = new QueueFamilies(Engine.getPhysicalDevice());
        graphicsQueueFamily = queueFamilies.findQueueFamily(VK_QUEUE_GRAPHICS_BIT, false);
        presentQueueFamily = queueFamilies.findQueueFamily(0, true);

        graphicsQueue = graphicsQueueFamily.getQueue(vkLogicalDevice, 0);
        presentQueue = presentQueueFamily.getQueue(vkLogicalDevice, 0);
    }

    /**
     * Locks the executing thread until the main window is closed or {@link Engine#terminate()} is called from another thread.
     * While locked, code can be executed on this thread through {@link Engine#executeOnMainThread(Runnable)}. <br/>
     * Must be called from the main thread.
     * @param windowWidth Width of the new main window in pixels
     * @param windowHeight Height of the new main window in pixels
     */
    public static void start(int windowWidth, int windowHeight) {
        mainWindow = new Window(appName, windowWidth, windowHeight, false);
        glfwSetWindowCloseCallback(mainWindow.getId(), windowId -> terminate());

        Logger.log(LogLevel.INFO, "Engine", "Started");

        while (true) {
            try {
                mainThreadQueue.take().run();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void terminate() {
        executeOnMainThread(() -> {
            cleanup();
            Thread.currentThread().interrupt();
            Logger.log(LogLevel.INFO, "Engine", "Terminated");
        });
    }

    /**
     * Runs a task asynchronously on the main thread.
     * The main thread must be locked in {@link Engine#start(int, int)}.
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

    public static QueueFamily getGraphicsQueueFamily() {
        return graphicsQueueFamily;
    }
    static void setGraphicsQueueFamily(QueueFamily graphicsQueueFamily) {
        Engine.graphicsQueueFamily = graphicsQueueFamily;
    }

    public static QueueFamily getPresentQueueFamily() {
        return presentQueueFamily;
    }
    static void setPresentQueueFamily(QueueFamily presentQueueFamily) {
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
