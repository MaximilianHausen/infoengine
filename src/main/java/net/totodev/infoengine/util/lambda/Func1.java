package net.totodev.infoengine.util.lambda;

/**
 * Represents a method with return type and 1 parameter
 */
@FunctionalInterface
public interface Func1<TResult, TParam> {
    TResult run(TParam param);
}