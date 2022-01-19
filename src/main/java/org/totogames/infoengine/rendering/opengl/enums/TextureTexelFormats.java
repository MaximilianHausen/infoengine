package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum TextureTexelFormats {
    RED(GL_RED),
    GREEN(GL_GREEN),
    BLUE(GL_BLUE),
    ALPHA(GL_ALPHA),
    RG(GL_RG),
    RGB(GL_RGB),
    RGBA(GL_RGBA),
    BGR(GL_BGR),
    BGRA(GL_BGRA),
    RED_INTEGER(GL_RED_INTEGER),
    GREEN_INTEGER(GL_GREEN_INTEGER),
    BLUE_INTEGER(GL_BLUE_INTEGER),
    //ALPHA_INTEGER(GL_ALPHA_INTEGER),
    RG_INTEGER(GL_RG_INTEGER),
    RGB_INTEGER(GL_RGB_INTEGER),
    RGBA_INTEGER(GL_RGBA_INTEGER),
    BGR_INTEGER(GL_BGR_INTEGER),
    BGRA_INTEGER(GL_BGRA_INTEGER),
    STENCIL_INDEX(GL_STENCIL_INDEX),
    DEPTH_COMPONENT(GL_DEPTH_COMPONENT),
    DEPTH_STENCIL(GL_DEPTH_STENCIL);

    private final int value;

    TextureTexelFormats(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
