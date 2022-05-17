package net.totodev.infoengine.util.lambda;

/**
 * Represents a method without return type and 2 parameters
 */
@FunctionalInterface
public interface Action2<TParam1, TParam2> {
    void run(TParam1 param1, TParam2 param2);
}
