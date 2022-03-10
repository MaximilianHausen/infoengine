package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed framebuffer.
 */
public class FramebufferDisposedException extends DisposedException {
    public FramebufferDisposedException() {
        super("Framebuffer was already disposed");
    }
}
