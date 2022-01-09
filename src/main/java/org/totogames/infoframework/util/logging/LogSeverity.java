package org.totogames.infoframework.util.logging;

public enum LogSeverity {
    Debug(1),
    Info(2),
    Error(3),
    Critical(4);

    private int value;

    LogSeverity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
