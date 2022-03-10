package org.totogames.infoengine.rendering;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed window.
 */
public class WindowDisposedException extends DisposedException {
    public WindowDisposedException() {
        super("Window was already disposed");
    }
}
