package net.totodev.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum VertexAttribDataType {
    BYTE(GL_BYTE),
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
    SHORT(GL_SHORT),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
    INT(GL_INT),
    UNSIGNED_INT(GL_UNSIGNED_INT),
    HALF_FLOAT(GL_HALF_FLOAT),
    FLOAT(GL_FLOAT),
    DOUBLE(GL_DOUBLE),
    UNSIGNED_INT_2_10_10_10_REV(GL_UNSIGNED_INT_2_10_10_10_REV),
    INT_2_10_10_10_REV(GL_INT_2_10_10_10_REV),
    FIXED(GL_FIXED);

    private final int value;

    VertexAttribDataType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
