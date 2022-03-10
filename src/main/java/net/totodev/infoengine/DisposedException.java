package net.totodev.infoengine;

/**
 * This Exception is thrown when calling a method on a disposed object.
 */
public class DisposedException extends RuntimeException {
    public DisposedException(String message) {
        super(message);
    }
}
