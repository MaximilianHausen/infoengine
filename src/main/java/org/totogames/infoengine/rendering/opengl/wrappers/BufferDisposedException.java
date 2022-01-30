package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

public class BufferDisposedException extends DisposedException {
    public BufferDisposedException() {
        super("Buffer was already disposed");
    }
}
