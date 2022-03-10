package net.totodev.infoengine.util.logging;

import net.totodev.infoengine.util.Action1;
import org.jetbrains.annotations.NotNull;

/**
 * A simple customizable logger. Set the logging target with {@link #setLogTarget(Action1)}.
 */
public class Logger {
    private static LogSeverity logLevel = LogSeverity.Info;
    private static Action1<String> logTarget = System.out::println;

    /**
     * Sends this LogMessage as a string to the logging target.
     * @param message The LogMessage to send
     */
    public static void log(@NotNull LogMessage message) {
        if (message.severity.getValue() < logLevel.getValue())
            return;
        logTarget.run(message.toString());
    }

    /**
     * Constructs a LogMessage and send it as a string to the logging target.
     * @param severity The importance of the message
     * @param source   The source of the message
     * @param message  The message
     */
    public static void log(@NotNull LogSeverity severity, @NotNull String source, @NotNull String message) {
        log(new LogMessage(severity, source, message));
    }

    /**
     * Sets the minimum LogSeverity to log, everything below gets discarded.
     * @param minSeverity The new minimum severity
     */
    public static void setLogLevel(@NotNull LogSeverity minSeverity) {
        logLevel = minSeverity;
        log(LogSeverity.Info, "Logger", "LogLevel set to " + logLevel);
    }

    /**
     * Sets the target to send the messages to. Announces this change on both the new and the old target with a severity of Info.
     * @param target The new target
     */
    public static void setLogTarget(@NotNull Action1<String> target) {
        log(LogSeverity.Info, "Logger", "Log target changed. This target will no longer receive log messages.");
        logTarget = target;
        log(LogSeverity.Info, "Logger", "Log target changed. This target will now receive all log messages.");
    }
}
