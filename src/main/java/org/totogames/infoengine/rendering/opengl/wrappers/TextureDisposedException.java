package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

public class TextureDisposedException extends DisposedException {
    public TextureDisposedException() {
        super("Texture was already disposed");
    }
}
