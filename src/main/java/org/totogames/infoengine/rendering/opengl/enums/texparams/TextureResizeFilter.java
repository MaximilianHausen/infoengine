package org.totogames.infoengine.rendering.opengl.enums.texparams;

import static org.lwjgl.opengl.GL46C.GL_LINEAR;
import static org.lwjgl.opengl.GL46C.GL_NEAREST;

public enum TextureResizeFilter {
    Nearest(GL_NEAREST),
    Linear(GL_LINEAR);

    private final int value;

    TextureResizeFilter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
