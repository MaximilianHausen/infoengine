package net.totodev.infoengine.core;

import net.totodev.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed window.
 */
public class WindowDisposedException extends DisposedException {
    public WindowDisposedException() {
        super("Window was already disposed");
    }
}
