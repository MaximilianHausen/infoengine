package net.totodev.infoengine.core;

import net.totodev.infoengine.rendering.*;
import net.totodev.infoengine.util.SemVer;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.lwjgl.vulkan.VkInstance;

public class Engine {
    private static Window mainWindow;

    private static VkInstance vkInstance;
    private static long debugManager;

    public static void initialize(String appName, SemVer appVersion) {
        ImmutableSet<String> validationLayers = Sets.immutable.of("VK_LAYER_KHRONOS_validation");
        vkInstance = VkInstanceHelper.createInstance(appName, appVersion, validationLayers);
        debugManager = VkDebugUtilsHelper.createDebugMessenger(vkInstance);
    }
}
