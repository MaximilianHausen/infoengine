package org.totogames.infoengine.rendering.opengl.enums;

import java.util.Arrays;

import static org.lwjgl.opengl.GL46C.*;

public enum BufferAccessRestrictions {
    MAP_READ_BIT(GL_MAP_READ_BIT),
    MAP_WRITE_BIT(GL_MAP_WRITE_BIT),
    DYNAMIC_STORAGE_BIT(GL_DYNAMIC_STORAGE_BIT),
    MAP_PERSISTENT_BIT(GL_MAP_PERSISTENT_BIT),
    MAP_COHERENT_BIT(GL_MAP_COHERENT_BIT),
    CLIENT_STORAGE_BIT(GL_CLIENT_STORAGE_BIT);

    private final int value;

    BufferAccessRestrictions(int value) {
        this.value = value;
    }

    public static int combineFlags(BufferAccessRestrictions... flags) {
        return Arrays.stream(flags).map(BufferAccessRestrictions::getValue).reduce(0, (result, next) -> result | next);
    }

    public int getValue() {
        return value;
    }
}
