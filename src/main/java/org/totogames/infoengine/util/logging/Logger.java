package org.totogames.infoengine.util.logging;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.Action1;

public class Logger {
    private static LogSeverity logLevel = LogSeverity.Info;
    private static Action1<String> logTarget = System.out::println;

    public static void log(@NotNull LogMessage message) {
        if (message.severity.getValue() < logLevel.getValue())
            return;
        logTarget.run(message.toString());
    }

    public static void log(@NotNull LogSeverity severity, @NotNull String source, @NotNull String message) {
        log(new LogMessage(severity, source, message));
    }

    public static void setLogLevel(@NotNull LogSeverity minSeverity) {
        logLevel = minSeverity;
        log(LogSeverity.Info, "Logger", "LogLevel set to " + logLevel);
    }

    public static void setLogTarget(@NotNull Action1<String> target) {
        log(LogSeverity.Info, "Logger", "Log target changed. This target will no longer receive log messages");
        logTarget = target;
        log(LogSeverity.Info, "Logger", "Log target changed. This target will now receive all log messages");
    }
}
