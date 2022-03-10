package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed buffer.
 */
public class BufferDisposedException extends DisposedException {
    public BufferDisposedException() {
        super("Buffer was already disposed");
    }
}
