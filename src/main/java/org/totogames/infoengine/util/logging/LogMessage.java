package org.totogames.infoengine.util.logging;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

/**
 * Stores all information needed to print a message to the log.
 * Can be converted to its formatted string representation with {@link #toString()}.
 * @see Logger
 */
public class LogMessage {
    public LogSeverity severity;
    public String source;
    public String message;

    /**
     * @param severity The importance of the message
     * @param source The source of the message
     * @param message The main message
     */
    public LogMessage(@NotNull LogSeverity severity, @NotNull String source, @NotNull String message) {
        this.severity = severity;
        this.source = source;
        this.message = message;
    }

    /**
     * Returns a string representation of the message, correctly formatted for logging.
     * @return A string representation of the message
     */
    public @NotNull String toString() {
        String spacing = "  ";
        int sourceWidth = 15; // Width of the source part of the message. Sources longer than this get cut off

        String timestamp = LocalTime.now().toString().substring(0, 8);

        // Assemble source with padding
        StringBuilder source = new StringBuilder(this.source);
        while (source.length() < sourceWidth)
            source.append(" ");

        return timestamp + spacing + source + spacing + message;
    }
}
