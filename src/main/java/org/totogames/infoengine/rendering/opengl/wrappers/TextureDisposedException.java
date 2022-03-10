package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed texture.
 */
public class TextureDisposedException extends DisposedException {
    public TextureDisposedException() {
        super("Texture was already disposed");
    }
}
