package org.totogames.infoengine.rendering.opengl.wrappers;

import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.totogames.infoengine.rendering.opengl.enums.FramebufferAttachmentType;
import org.totogames.infoengine.rendering.opengl.enums.FramebufferBindTarget;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

/**
 * Java wrapper for OpenGL framebuffer objects
 * @see <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">OpenGL Wiki: Framebuffers</a>
 */
public class Framebuffer implements IOglObject {
    private final static HashBiMap<Framebuffer, FramebufferBindTarget> bindStatus = HashBiMap.create();
    private final int id;
    private boolean isDisposed = false;

    public Framebuffer() {
        id = glGenFramebuffers();
        Logger.log(LogSeverity.Debug, "OpenGL", "Framebuffer created with id " + id);
    }

    /**
     * Gets the currently bound framebuffer
     * @param target The target to get the framebuffer from
     * @return The framebuffer, or null if nothing is bound on that target
     */
    public static @Nullable Framebuffer getBoundFramebuffer(@NotNull FramebufferBindTarget target) {
        return bindStatus.inverse().get(target);
    }

    /**
     * Binds this framebuffer to a buffer bind target.
     * @param target The target to bind to
     */
    public void bind(FramebufferBindTarget target) {
        if (isDisposed) throw new FramebufferDisposedException();
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        bindStatus.forcePut(this, target);

        Logger.log(LogSeverity.Trace, "OpenGL", "Framebuffer " + id + " bound to target " + target);
    }
    /**
     * Unbinds this framebuffer from the targets it is currently bound to
     */
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new FramebufferDisposedException();
        if (bindStatus.containsKey(this)) {
            FramebufferBindTarget target = bindStatus.get(this);
            glBindFramebuffer(target.getValue(), 0);
            bindStatus.remove(this);
            Logger.log(LogSeverity.Trace, "OpenGL", "Framebuffer " + id + " unbound from target " + target);
        }
    }

    public @Nullable FramebufferBindTarget getBindStatus() {
        if (isDisposed) throw new FramebufferDisposedException();
        return bindStatus.get(this);
    }

    /**
     * Attaches a texture to this framebuffer. It will then be rendered to.
     * @param tex            The texture to attach
     * @param attachmentType Where to attach the texture
     */
    @RequiresBind
    public void attachTexture(@NotNull Texture2d tex, @NotNull FramebufferAttachmentType attachmentType) {
        if (isDisposed) throw new FramebufferDisposedException();
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType.getValue(), GL_TEXTURE_2D, tex.getId(), 0); // TODO: Mipmaps
    }

    public int getId() {
        if (isDisposed) throw new FramebufferDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new FramebufferDisposedException();
        glDeleteFramebuffers(id);
        bindStatus.remove(this);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "OpenGL", "Framebuffer deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
