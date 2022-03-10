package org.totogames.infoengine.rendering;

/**
 * This can be rendered to (Probably an awful solution, but it works until I set up a proper rendering pipeline)
 */
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
