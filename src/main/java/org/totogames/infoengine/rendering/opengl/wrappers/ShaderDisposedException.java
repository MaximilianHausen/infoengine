package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed shader.
 */
public class ShaderDisposedException extends DisposedException {
    public ShaderDisposedException() {
        super("Shader was already disposed");
    }
}
