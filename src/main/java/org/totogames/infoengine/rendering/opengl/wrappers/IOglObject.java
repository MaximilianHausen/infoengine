package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.IDisposable;

/**
 * A disposable object with an id
 */
public interface IOglObject extends IDisposable {
    int getId();
}
