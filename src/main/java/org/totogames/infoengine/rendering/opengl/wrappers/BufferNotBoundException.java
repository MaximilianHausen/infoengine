package org.totogames.infoengine.rendering.opengl.wrappers;

public class BufferNotBoundException extends NotBoundException {
    public BufferNotBoundException() {
        super("The buffer must be bound before changing its state");
    }
}
