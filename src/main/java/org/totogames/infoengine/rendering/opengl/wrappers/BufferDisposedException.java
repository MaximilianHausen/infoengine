package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed buffer.
 */
public class BufferDisposedException extends DisposedException {
    public BufferDisposedException() {
        super("Buffer was already disposed");
    }
}
