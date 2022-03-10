package net.totodev.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum FramebufferAttachmentType {
    //TODO: Multiple color attachments
    Color(GL_COLOR_ATTACHMENT0),
    Depth(GL_DEPTH_ATTACHMENT),
    Stencil(GL_STENCIL_ATTACHMENT),
    DepthStencil(GL_DEPTH_STENCIL_ATTACHMENT);

    private final int value;

    FramebufferAttachmentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
