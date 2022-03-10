package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.IDisposable;

/**
 * A disposable object with an id
 */
public interface IOglObject extends IDisposable {
    int getId();
}
