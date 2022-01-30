package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.DisposedException;
import org.totogames.infoengine.rendering.opengl.enums.FramebufferAttachmentType;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

public class Framebuffer implements IOglObject {
    private final int id;
    private boolean isDisposed = false;

    public Framebuffer() {
        id = glGenFramebuffers();
        Logger.log(LogSeverity.Debug, "Framebuffer", "Framebuffer created with id " + id);
    }

    public static void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        if (isDisposed) throw new DisposedException("Framebuffer was already disposed");
        glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, getId());
    }

    @RequiresBind
    public void attachTexture(@NotNull Texture2d tex, @NotNull FramebufferAttachmentType attachmentType) {
        if (isDisposed) throw new DisposedException("Framebuffer was already disposed");
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType.getValue(), GL_TEXTURE_2D, tex.getId(), 0); // TODO: Mipmaps
    }

    public int getId() {
        if (isDisposed) throw new DisposedException("Framebuffer was already disposed");
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new DisposedException("Framebuffer was already disposed");
        glDeleteFramebuffers(id);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "Framebuffer", "Framebuffer deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}