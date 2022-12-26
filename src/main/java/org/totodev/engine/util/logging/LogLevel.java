package org.totodev.engine.util.logging;

/**
 * Represents the importance of a log message.
 * Used by the {@link Logger} to discard unimportant messages
 */
public enum LogLevel {
    TRACE(0),
    DEBUG(1),
    INFO(2),
    ERROR(3),
    CRITICAL(4);

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return switch (this) {
            case TRACE -> "Trace";
            case DEBUG -> "Debug";
            case INFO -> "Info ";
            case ERROR -> "Error";
            case CRITICAL -> "Crit ";
        };
    }
}
