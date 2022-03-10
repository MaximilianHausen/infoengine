package net.totodev.infoengine.rendering.opengl.enums;

import net.totodev.infoengine.rendering.opengl.enums.custom.BufferUsageFrequency;
import net.totodev.infoengine.rendering.opengl.enums.custom.BufferUsageType;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL46C.*;

public enum BufferUsage {
    STATIC_DRAW(GL_STATIC_DRAW),
    STATIC_READ(GL_STATIC_READ),
    STATIC_COPY(GL_STATIC_COPY),
    DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
    DYNAMIC_READ(GL_DYNAMIC_READ),
    DYNAMIC_COPY(GL_DYNAMIC_COPY),
    STREAM_DRAW(GL_STREAM_DRAW),
    STREAM_READ(GL_STREAM_READ),
    STREAM_COPY(GL_STREAM_COPY);

    private final int value;

    BufferUsage(int value) {
        this.value = value;
    }

    public static @NotNull BufferUsage fromComponents(@NotNull BufferUsageFrequency frequency, @NotNull BufferUsageType type) {
        if (frequency == BufferUsageFrequency.STATIC && type == BufferUsageType.DRAW) return BufferUsage.STATIC_DRAW;
        else if (frequency == BufferUsageFrequency.STATIC && type == BufferUsageType.READ)
            return BufferUsage.STATIC_READ;
        else if (frequency == BufferUsageFrequency.STATIC && type == BufferUsageType.COPY)
            return BufferUsage.STATIC_COPY;
        else if (frequency == BufferUsageFrequency.DYNAMIC && type == BufferUsageType.DRAW)
            return BufferUsage.DYNAMIC_DRAW;
        else if (frequency == BufferUsageFrequency.DYNAMIC && type == BufferUsageType.READ)
            return BufferUsage.DYNAMIC_READ;
        else if (frequency == BufferUsageFrequency.DYNAMIC && type == BufferUsageType.COPY)
            return BufferUsage.DYNAMIC_COPY;
        else if (frequency == BufferUsageFrequency.STREAM && type == BufferUsageType.DRAW)
            return BufferUsage.STREAM_DRAW;
        else if (frequency == BufferUsageFrequency.STREAM && type == BufferUsageType.READ)
            return BufferUsage.STREAM_READ;
        else return BufferUsage.STREAM_COPY;
    }

    public int getValue() {
        return value;
    }
}
