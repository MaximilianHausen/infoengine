package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum BufferBindTarget {
    ARRAY_BUFFER(GL_ARRAY_BUFFER),
    ELEMENT_ARRAY_BUFFER(GL_ELEMENT_ARRAY_BUFFER),
    COPY_READ_BUFFER(GL_COPY_READ_BUFFER),
    COPY_WRITE_BUFFER(GL_COPY_WRITE_BUFFER),
    PIXEL_PACK_BUFFER(GL_PIXEL_PACK_BUFFER),
    PIXEL_UNPACK_BUFFER(GL_PIXEL_UNPACK_BUFFER),
    QUERY_BUFFER(GL_QUERY_BUFFER),
    TEXTURE_BUFFER(GL_TEXTURE_BUFFER),
    TRANSFORM_FEEDBACK_BUFFER(GL_TRANSFORM_FEEDBACK_BUFFER),
    UNIFORM_BUFFER(GL_UNIFORM_BUFFER),
    DRAW_INDIRECT_BUFFER(GL_DRAW_INDIRECT_BUFFER),
    ATOMIC_COUNTER_BUFFER(GL_ATOMIC_COUNTER_BUFFER),
    DISPATCH_INDIRECT_BUFFER(GL_DISPATCH_INDIRECT_BUFFER),
    SHADER_STORAGE_BUFFER(GL_SHADER_STORAGE_BUFFER);

    private final int value;

    BufferBindTarget(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}