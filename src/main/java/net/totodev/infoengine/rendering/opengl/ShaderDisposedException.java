package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed shader.
 */
public class ShaderDisposedException extends DisposedException {
    public ShaderDisposedException() {
        super("Shader was already disposed");
    }
}
