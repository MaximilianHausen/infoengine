package org.totogames.infoengine.util;

@FunctionalInterface
public interface Func2<TResult, TParam1, TParam2> {
    TResult run(TParam1 param1, TParam2 param2);
}
