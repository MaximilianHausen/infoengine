package net.totodev.infoengine.util.lambda;

/**
 * Represents a method without return type and 3 parameters
 */
@FunctionalInterface
public interface Action3<TParam1, TParam2, TParam3> {
    void run(TParam1 param1, TParam2 param2, TParam3 param3);
}
