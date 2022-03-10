package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed program.
 */
public class ProgramDisposedException extends DisposedException {
    public ProgramDisposedException() {
        super("Program was already disposed");
    }
}
