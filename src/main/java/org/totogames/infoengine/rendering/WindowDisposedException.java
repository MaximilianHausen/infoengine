package org.totogames.infoengine.rendering;

import org.totogames.infoengine.DisposedException;

public class WindowDisposedException extends DisposedException {
    public WindowDisposedException() {
        super("Window was already disposed");
    }
}
