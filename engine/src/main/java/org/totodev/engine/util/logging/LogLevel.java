package org.totodev.engine.util.logging;

/**
 * Represents the importance of a log message.
 * Used by the {@link Logger} to discard unimportant messages
 */
public enum LogLevel {
    Trace(0),
    Debug(1),
    Info(2),
    Error(3),
    Critical(4);

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
            case Trace -> "Trace";
            case Debug -> "Debug";
            case Info -> "Info ";
            case Error -> "Error";
            case Critical -> "Crit ";
        };
    }
}
