package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed vertex array.
 */
public class VertexArrayDisposedException extends DisposedException {
    public VertexArrayDisposedException() {
        super("VertexArray was already disposed");
    }
}
