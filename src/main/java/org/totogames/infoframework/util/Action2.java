package org.totogames.infoframework.util;

@FunctionalInterface
public interface Action2<TParam1, TParam2> {
    void run(TParam1 param1, TParam2 param2);
}
