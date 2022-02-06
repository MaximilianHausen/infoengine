package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

public class VertexArrayDisposedException extends DisposedException {
    public VertexArrayDisposedException() {
        super("VertexArray was already disposed");
    }
}
