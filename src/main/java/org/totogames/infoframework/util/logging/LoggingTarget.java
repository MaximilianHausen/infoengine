package org.totogames.infoframework.util.logging;

@FunctionalInterface
public interface LoggingTarget {
    void log(String logEntry);
}
