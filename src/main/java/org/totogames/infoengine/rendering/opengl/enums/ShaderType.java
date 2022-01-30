package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum ShaderType {
    VERTEX_SHADER(GL_VERTEX_SHADER),
    FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
    GEOMETRY_SHADER(GL_GEOMETRY_SHADER),
    TESS_CONTROL_SHADER(GL_TESS_CONTROL_SHADER),
    TESS_EVALUATION_SHADER(GL_TESS_EVALUATION_SHADER);

    private final int value;

    ShaderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
