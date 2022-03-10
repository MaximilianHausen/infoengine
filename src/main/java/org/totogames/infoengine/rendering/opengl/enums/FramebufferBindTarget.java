package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum FramebufferBindTarget {
    FRAMEBUFFER(GL_FRAMEBUFFER),
    READ_FRAMEBUFFER(GL_READ_FRAMEBUFFER),
    DRAW_FRAMEBUFFER(GL_DRAW_FRAMEBUFFER);

    private final int value;

    FramebufferBindTarget(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
