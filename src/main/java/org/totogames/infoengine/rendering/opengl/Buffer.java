package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.DisposedException;
import org.totogames.infoengine.rendering.opengl.enums.BufferBindTargets;
import org.totogames.infoengine.rendering.opengl.enums.BufferOptimisationMode;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

// TODO: Bind to any target
public class Buffer implements IOglObject {
    private final int id;
    private boolean isDisposed = false;
    private final BufferBindTargets bufferType;

    public Buffer(BufferBindTargets bufferType) {
        id = glGenBuffers();
        this.bufferType = bufferType;
        Logger.log(LogSeverity.Debug, "Buffer", "Buffer created with id " + id);
    }

    public static void unbind(@NotNull BufferBindTargets target) {
        glBindBuffer(target.getValue(), 0);
    }

    public void bind(@NotNull BufferBindTargets target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBindBuffer(target.getValue(), id);
    }

    public BufferBindTargets getBufferType() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        return bufferType;
    }

    @RequiresBind
    public void setData(long size, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), size, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(@NotNull ByteBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(@NotNull ShortBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(@NotNull IntBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(@NotNull LongBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(@NotNull FloatBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(@NotNull DoubleBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(short[] data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(int[] data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(long[] data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(float[] data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    @RequiresBind
    public void setData(double[] data, @NotNull BufferOptimisationMode optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }

    public int getId() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glDeleteBuffers(id);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "Buffer", "Buffer deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
