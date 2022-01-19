package org.totogames.infoengine.util;

@FunctionalInterface
public interface Action1<TParam> {
    void run(TParam param);
}
