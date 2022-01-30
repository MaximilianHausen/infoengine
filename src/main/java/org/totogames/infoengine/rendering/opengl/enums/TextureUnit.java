package org.totogames.infoengine.rendering.opengl.enums;

import org.jetbrains.annotations.Range;

import static org.lwjgl.opengl.GL46C.*;

public enum TextureUnit {
    TEXTURE0(GL_TEXTURE0),
    TEXTURE1(GL_TEXTURE1),
    TEXTURE2(GL_TEXTURE2),
    TEXTURE3(GL_TEXTURE3),
    TEXTURE4(GL_TEXTURE4),
    TEXTURE5(GL_TEXTURE5),
    TEXTURE6(GL_TEXTURE6),
    TEXTURE7(GL_TEXTURE7),
    TEXTURE8(GL_TEXTURE8),
    TEXTURE9(GL_TEXTURE9),
    TEXTURE10(GL_TEXTURE10),
    TEXTURE11(GL_TEXTURE11),
    TEXTURE12(GL_TEXTURE12),
    TEXTURE13(GL_TEXTURE13),
    TEXTURE14(GL_TEXTURE14),
    TEXTURE15(GL_TEXTURE15),
    TEXTURE16(GL_TEXTURE16),
    TEXTURE17(GL_TEXTURE17),
    TEXTURE18(GL_TEXTURE18),
    TEXTURE19(GL_TEXTURE19),
    TEXTURE20(GL_TEXTURE20),
    TEXTURE21(GL_TEXTURE21),
    TEXTURE22(GL_TEXTURE22),
    TEXTURE23(GL_TEXTURE23),
    TEXTURE24(GL_TEXTURE24),
    TEXTURE25(GL_TEXTURE25),
    TEXTURE26(GL_TEXTURE26),
    TEXTURE27(GL_TEXTURE27),
    TEXTURE28(GL_TEXTURE28),
    TEXTURE29(GL_TEXTURE29),
    TEXTURE30(GL_TEXTURE30),
    TEXTURE31(GL_TEXTURE31);

    private final int value;

    TextureUnit(int value) {
        this.value = value;
    }

    public static TextureUnit fromNumber(@Range(from = 0, to = 31) int number) {
        return switch (number) {
            case 1 -> TEXTURE1;
            case 2 -> TEXTURE2;
            case 3 -> TEXTURE3;
            case 4 -> TEXTURE4;
            case 5 -> TEXTURE5;
            case 6 -> TEXTURE6;
            case 7 -> TEXTURE7;
            case 8 -> TEXTURE8;
            case 9 -> TEXTURE9;
            case 10 -> TEXTURE10;
            case 11 -> TEXTURE11;
            case 12 -> TEXTURE12;
            case 13 -> TEXTURE13;
            case 14 -> TEXTURE14;
            case 15 -> TEXTURE15;
            case 16 -> TEXTURE16;
            case 17 -> TEXTURE17;
            case 18 -> TEXTURE18;
            case 19 -> TEXTURE19;
            case 20 -> TEXTURE20;
            case 21 -> TEXTURE21;
            case 22 -> TEXTURE22;
            case 23 -> TEXTURE23;
            case 24 -> TEXTURE24;
            case 25 -> TEXTURE25;
            case 26 -> TEXTURE26;
            case 27 -> TEXTURE27;
            case 28 -> TEXTURE28;
            case 29 -> TEXTURE29;
            case 30 -> TEXTURE30;
            case 31 -> TEXTURE31;
            default -> TEXTURE0;
        };
    }

    public @Range(from = 0, to = 31) int toNumber() {
        return switch (this) {
            case TEXTURE1 -> 1;
            case TEXTURE2 -> 2;
            case TEXTURE3 -> 3;
            case TEXTURE4 -> 4;
            case TEXTURE5 -> 5;
            case TEXTURE6 -> 6;
            case TEXTURE7 -> 7;
            case TEXTURE8 -> 8;
            case TEXTURE9 -> 9;
            case TEXTURE10 -> 10;
            case TEXTURE11 -> 11;
            case TEXTURE12 -> 12;
            case TEXTURE13 -> 13;
            case TEXTURE14 -> 14;
            case TEXTURE15 -> 15;
            case TEXTURE16 -> 16;
            case TEXTURE17 -> 17;
            case TEXTURE18 -> 18;
            case TEXTURE19 -> 19;
            case TEXTURE20 -> 20;
            case TEXTURE21 -> 21;
            case TEXTURE22 -> 22;
            case TEXTURE23 -> 23;
            case TEXTURE24 -> 24;
            case TEXTURE25 -> 25;
            case TEXTURE26 -> 26;
            case TEXTURE27 -> 27;
            case TEXTURE28 -> 28;
            case TEXTURE29 -> 29;
            case TEXTURE30 -> 30;
            case TEXTURE31 -> 31;
            default -> 0;
        };
    }

    public int getValue() {
        return value;
    }
}
