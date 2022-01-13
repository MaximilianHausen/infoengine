package org.totogames.infoframework.util.logging;

import org.totogames.infoframework.util.Action1;

public class Logger {
    private static LogSeverity logLevel = LogSeverity.Info;
    private static Action1<String> logTarget = System.out::println;

    public static void log(LogMessage message) {
        if (message.severity.getValue() < logLevel.getValue())
            return;
        logTarget.run(message.toString());
    }

    public static void log(LogSeverity severity, String source, String message) {
        log(new LogMessage(severity, source, message));
    }

    public static void setLogLevel(LogSeverity minSeverity) {
        logLevel = minSeverity;
        log(LogSeverity.Info, "Logger", "LogLevel set to " + logLevel);
    }

    public static void setLogTarget(Action1<String> target) {
        log(LogSeverity.Info, "Logger", "Log target changed. This target will no longer receive log messages");
        logTarget = target;
        log(LogSeverity.Info, "Logger", "Log target changed. This target will now receive all log messages");
    }
}
