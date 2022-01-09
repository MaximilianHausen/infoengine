package org.totogames.infoframework.util.logging;

import java.time.LocalTime;

public class LogMessage {
    public LogSeverity severity;
    public String source;
    public String message;

    public LogMessage(LogSeverity severity, String source, String message) {
        this.severity = severity;
        this.source = source;
        this.message = message;
    }

    public String toString() {
        String timestamp = LocalTime.now().toString().substring(0, 8);
        String spacing = "  ";

        int sourceWidth = 15; // Everything after this gets cut off
        StringBuilder source = new StringBuilder(this.source);
        while (source.length() < sourceWidth)
            source.append(" ");

        return timestamp + spacing + source + spacing + message;
    }
}
