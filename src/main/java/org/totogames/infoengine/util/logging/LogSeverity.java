package org.totogames.infoengine.util.logging;

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
