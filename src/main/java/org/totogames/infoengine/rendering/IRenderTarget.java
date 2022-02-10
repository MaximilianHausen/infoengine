package org.totogames.infoengine.rendering;

public interface IRenderTarget {
    /**
     * Activate this target (bind the framebuffer)
     */
    void activate();

    /**
     * Gets called after a frame was rendered to this target
     */
    void renderedFrame();
}
