package net.totodev.infoengine.util.logging;

/**
 * Represents the importance/severity of a log message.
 * Used by the {@link Logger} to discard unimportant messages
 */
public enum LogSeverity {
    Trace(0),
    Debug(1),
    Info(2),
    Error(3),
    Critical(4);

    private final int value;

    LogSeverity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
