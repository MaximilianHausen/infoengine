package org.totodev.engine.util.logging;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

import java.time.LocalTime;
import java.util.function.Consumer;

import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.VK_FALSE;

/**
 * A simple customizable logger. Set the logging target with {@link #setLogTarget(Consumer)}.
 */
public class Logger {
    private static LogLevel minLogLevel = LogLevel.INFO;
    private static Consumer<String> logTarget = System.out::println;
    private static Consumer<String> errLogTarget = System.err::println;

    /**
     * Formats a log message and send it to the respective logging target.
     * @param logLevel The importance of the message
     * @param source   The source of the message
     * @param message  The message
     */
    public static void log(@NotNull LogLevel logLevel, @NotNull String source, @NotNull String message) {
        if (logLevel.getValue() < minLogLevel.getValue())
            return;
        if (logLevel.getValue() > 2)
            errLogTarget.accept(formatMessage(logLevel, source, message));
        else
            logTarget.accept(formatMessage(logLevel, source, message));
    }

    /**
     * Sets the minimum log level to log, everything below gets discarded.
     * @param minLevel The new minimum log level
     */
    public static void setLogLevel(@NotNull LogLevel minLevel) {
        minLogLevel = minLevel;
        log(LogLevel.INFO, "Logger", "LogLevel set to " + minLogLevel + ".");
    }

    /**
     * Sets the target to send the normal messages to. Announces this change on both the new and the old target with a log level of Info.
     * @param target The new target
     */
    public static void setLogTarget(@NotNull Consumer<String> target) {
        log(LogLevel.INFO, "Logger", "Log target changed. This target will no longer receive log messages.");
        logTarget = target;
        log(LogLevel.INFO, "Logger", "Log target changed. This target will now receive all log messages.");
    }

    /**
     * Sets the target to send the error messages to. Announces this change on the non-error target with a log level of Info.
     * @param target The new error target
     */
    public static void setErrLogTarget(@NotNull Consumer<String> target) {
        errLogTarget = target;
        log(LogLevel.INFO, "Logger", "Error log target changed.");
    }

    private static @NotNull String formatMessage(@NotNull LogLevel logLevel, @NotNull String source, @NotNull String message) {
        String spacing = " ";
        int sourceWidth = 12; // Width of the source part of the message. Sources longer than this get cut off

        String timestamp = LocalTime.now().toString().substring(0, 8);

        // Assemble source with padding
        StringBuilder sourceBuilder = new StringBuilder(source);
        while (sourceBuilder.length() < sourceWidth)
            sourceBuilder.append(" ");

        String paddedSource = sourceBuilder.toString();
        if (paddedSource.length() > sourceWidth)
            paddedSource = paddedSource.substring(0, 10);

        return timestamp + spacing + logLevel + spacing + paddedSource + spacing + message;
    }

    public static int vkLoggingCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

        LogLevel logLevel = switch (messageSeverity) {
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> LogLevel.TRACE;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> LogLevel.INFO;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> LogLevel.ERROR;
            default -> LogLevel.DEBUG;
        };
        Logger.log(logLevel, "Vulkan", callbackData.pMessageString());

        return VK_FALSE;
    }
}
