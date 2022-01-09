package org.totogames.infoframework.util.logging;

public class Logger {
    private static LogSeverity logLevel = LogSeverity.Info;
    private static LoggingTarget logTarget = System.out::println;

    public static void log(LogMessage message) {
        if (message.severity.getValue() < logLevel.getValue())
            return;
        logTarget.log(message.toString());
    }

    public static void log(LogSeverity severity, String source, String message) {
        log(new LogMessage(severity, source, message));
    }

    public static void setLogLevel(LogSeverity minSeverity) {
        logLevel = minSeverity;
        log(LogSeverity.Info, "Logger", "LogLevel set to " + logLevel);
    }

    public static void setLogTarget(LoggingTarget target) {
        logTarget = target;
    }
}
