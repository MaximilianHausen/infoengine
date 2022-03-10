package net.totodev.infoengine.util;

/**
 * Represents a method with return type and no parameters
 */
@FunctionalInterface
public interface Func<TResult> {
    TResult run();
}
