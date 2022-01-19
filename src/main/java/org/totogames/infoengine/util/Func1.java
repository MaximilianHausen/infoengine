package org.totogames.infoengine.util;

@FunctionalInterface
public interface Func1<TResult, TParam> {
    TResult run(TParam param);
}
