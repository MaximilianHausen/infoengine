package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.rendering.opengl.enums.FramebufferAttachmentTypes;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

public class Framebuffer implements IOglObject {
    private int id;

    public Framebuffer() {
        id = glGenFramebuffers();
        Logger.log(LogSeverity.Debug, "Framebuffer", "Framebuffer created in slot " + id);
    }

    public static void bindDefault() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, getId());
    }

    public int getId() {
        return id;
    }

    public void attachTexture(@NotNull Texture2d tex, @NotNull FramebufferAttachmentTypes attachmentType) {
        bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType.getValue(), GL_TEXTURE_2D, tex.getId(), 0); // TODO: Mipmaps
    }

    public void dispose() {
        Logger.log(LogSeverity.Debug, "Framebuffer", "Framebuffer deleted from slot " + id);
        if (id != -1) {
            glDeleteFramebuffers(id);
            id = -1;
        }
    }
}
