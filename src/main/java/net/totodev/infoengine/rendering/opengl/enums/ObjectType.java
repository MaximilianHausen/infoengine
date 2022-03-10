package net.totodev.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum ObjectType {
    BUFFER(GL_BUFFER),
    SHADER(GL_SHADER),
    PROGRAM(GL_PROGRAM),
    VERTEX_ARRAY(GL_VERTEX_ARRAY),
    QUERY(GL_QUERY),
    PROGRAM_PIPELINE(GL_PROGRAM_PIPELINE),
    TRANSFORM_FEEDBACK(GL_TRANSFORM_FEEDBACK),
    SAMPLER(GL_SAMPLER),
    TEXTURE(GL_TEXTURE),
    RENDERBUFFER(GL_RENDERBUFFER),
    FRAMEBUFFER(GL_FRAMEBUFFER);

    private final int value;

    ObjectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
