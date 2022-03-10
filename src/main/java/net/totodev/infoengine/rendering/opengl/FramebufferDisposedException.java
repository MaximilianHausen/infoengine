package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed framebuffer.
 */
public class FramebufferDisposedException extends DisposedException {
    public FramebufferDisposedException() {
        super("Framebuffer was already disposed");
    }
}
