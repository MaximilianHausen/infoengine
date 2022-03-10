package net.totodev.infoengine;

/**
 * This needs to be disposed manually, similar to {@link java.io.Closeable}.
 */
public interface IDisposable {
    /**
     * Dispose this object and free all used resources
     */
    void dispose();

    boolean isDisposed();
}
