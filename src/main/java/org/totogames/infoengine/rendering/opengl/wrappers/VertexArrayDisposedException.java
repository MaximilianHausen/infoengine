package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed vertex array.
 */
public class VertexArrayDisposedException extends DisposedException {
    public VertexArrayDisposedException() {
        super("VertexArray was already disposed");
    }
}
