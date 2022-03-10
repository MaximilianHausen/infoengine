package net.totodev.infoengine.rendering.opengl.enums.texparams;

import static org.lwjgl.opengl.GL46.*;

public enum TextureWrappingStyle {
    Repeat(GL_REPEAT),
    MirroredRepeat(GL_MIRRORED_REPEAT),
    ClampToEdge(GL_CLAMP_TO_EDGE),
    MirroredClampToEdge(GL_MIRROR_CLAMP_TO_EDGE),
    ClampToBorder(GL_CLAMP_TO_BORDER),
    MirroredClampToBorder(GL_MIRROR_CLAMP_TO_EDGE);

    private final int value;

    TextureWrappingStyle(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
