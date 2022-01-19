package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.GL_LINEAR;
import static org.lwjgl.opengl.GL46C.GL_NEAREST;

public enum TextureResizeFilters {
    Nearest(GL_NEAREST),
    Linear(GL_LINEAR);

    private final int value;

    TextureResizeFilters(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
