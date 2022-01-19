package org.totogames.infoengine.rendering.opengl.enums;

import static org.lwjgl.opengl.GL46C.*;

public enum FramebufferAttachmentTypes {
    //TODO: Multiple color attachments
    Color(GL_COLOR_ATTACHMENT0),
    Depth(GL_DEPTH_ATTACHMENT),
    Stencil(GL_STENCIL_ATTACHMENT),
    DepthStencil(GL_DEPTH_STENCIL_ATTACHMENT);

    private final int value;

    FramebufferAttachmentTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
