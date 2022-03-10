package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.DisposedException;

/**
 * This Exception is thrown when calling a method on a disposed program.
 */
public class ProgramDisposedException extends DisposedException {
    public ProgramDisposedException() {
        super("Program was already disposed");
    }
}
