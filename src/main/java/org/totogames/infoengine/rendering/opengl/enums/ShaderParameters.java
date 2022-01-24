package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum ShaderParameters {
    SHADER_TYPE(GL_SHADER_TYPE),
    DELETE_STATUS(GL_DELETE_STATUS),
    COMPILE_STATUS(GL_COMPILE_STATUS),
    INFO_LOG_LENGTH(GL_INFO_LOG_LENGTH),
    SHADER_SOURCE_LENGTH(GL_SHADER_SOURCE_LENGTH);

    private final int value;

    ShaderParameters(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
