package net.totodev.infoengine.util.lambda;

/**
 * Represents a method without return type and 1 parameter
 */
@FunctionalInterface
public interface Action1<TParam> {
    void run(TParam param);
}