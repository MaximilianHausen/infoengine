package org.totogames.infoframework.util;

@FunctionalInterface
public interface Action1<TParam> {
    void run(TParam param);
}
