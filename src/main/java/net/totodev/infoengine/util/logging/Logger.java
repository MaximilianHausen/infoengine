package net.totodev.infoengine.util.logging;

import net.totodev.infoengine.util.Action1;
import org.jetbrains.annotations.NotNull;

/**
 * A simple customizable logger. Set the logging target with {@link #setLogTarget(Action1)}.
 */
public class Logger {
    private static LogLevel logLevel = LogLevel.Info;
    private static Action1<String> logTarget = System.out::println;
    private static Action1<String> errLogTarget = System.err::println;

    /**
     * Sends this log message as a string to the logging target.
     * @param message The LogMessage to send
     */
    public static void log(@NotNull LogMessage message) {
        if (message.logLevel.getValue() < logLevel.getValue())
            return;
        if (message.logLevel.getValue() > 2)
            errLogTarget.run(message.toString());
        else
            logTarget.run(message.toString());
    }

    /**
     * Constructs a log message and send it as a string to the logging target.
     * @param severity The importance of the message
     * @param source   The source of the message
     * @param message  The message
     */
    public static void log(@NotNull LogLevel severity, @NotNull String source, @NotNull String message) {
        log(new LogMessage(severity, source, message));
    }

    /**
     * Sets the minimum log level to log, everything below gets discarded.
     * @param minLevel The new minimum log level
     */
    public static void setLogLevel(@NotNull LogLevel minLevel) {
        logLevel = minLevel;
        log(LogLevel.Info, "Logger", "LogLevel set to " + logLevel);
    }

    /**
     * Sets the target to send the normal messages to. Announces this change on both the new and the old target with a log level of Info.
     * @param target The new target
     */
    public static void setLogTarget(@NotNull Action1<String> target) {
        log(LogLevel.Info, "Logger", "Log target changed. This target will no longer receive log messages.");
        logTarget = target;
        log(LogLevel.Info, "Logger", "Log target changed. This target will now receive all log messages.");
    }

    /**
     * Sets the target to send the error messages to. Announces this change on the non-error target with a log level of Info.
     * @param target The new error target
     */
    public static void setErrLogTarget(@NotNull Action1<String> target) {
        errLogTarget = target;
        log(LogLevel.Info, "Logger", "Error log target changed");
    }
}
