package net.totodev.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum TextureType {
    TEXTURE_1D(GL_TEXTURE_1D),
    TEXTURE_2D(GL_TEXTURE_2D),
    TEXTURE_3D(GL_TEXTURE_3D),
    TEXTURE_RECTANGLE(GL_TEXTURE_RECTANGLE),
    TEXTURE_BUFFER(GL_TEXTURE_BUFFER),
    TEXTURE_CUBE_MAP(GL_TEXTURE_CUBE_MAP),
    TEXTURE_1D_ARRAY(GL_TEXTURE_1D_ARRAY),
    TEXTURE_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
    TEXTURE_CUBE_MAP_ARRAY(GL_TEXTURE_CUBE_MAP_ARRAY),
    TEXTURE_2D_MULTISAMPLE(GL_TEXTURE_2D_MULTISAMPLE),
    TEXTURE_2D_MULTISAMPLE_ARRAY(GL_TEXTURE_2D_MULTISAMPLE_ARRAY);

    private final int value;

    TextureType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
