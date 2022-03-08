package org.totogames.infoengine.util;

/**
 * Represents a method without return type and 4 parameters
 */
@FunctionalInterface
public interface Action4<TParam1, TParam2, TParam3, TParam4> {
    void run(TParam1 param1, TParam2 param2, TParam3 param3, TParam4 param4);
}
