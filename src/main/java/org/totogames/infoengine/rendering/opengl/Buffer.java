package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.rendering.opengl.enums.BufferBindTargets;
import org.totogames.infoengine.rendering.opengl.enums.BufferOptimisationMode;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

// TODO: Bind to any target
public class Buffer implements IOglObject {
    private final int id;
    private final BufferBindTargets bufferType;

    public Buffer(BufferBindTargets bufferType) {
        id = glGenBuffers();
        this.bufferType = bufferType;
    }

    public void bind(BufferBindTargets target) {
        glBindBuffer(target.getValue(), id);
    }

    public int getId() {
        return id;
    }

    public void dispose() {
        glDeleteBuffers(id);
    }

    public void setData(long size, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), size, optimisationMode.getValue());
    }
    public void setData(@NotNull ByteBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(@NotNull ShortBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(@NotNull IntBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(@NotNull LongBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(@NotNull FloatBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(@NotNull DoubleBuffer data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(short[] data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(int[] data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(long[] data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(float[] data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
    void setData(double[] data, @NotNull BufferOptimisationMode optimisationMode) {
        glBufferData(bufferType.getValue(), data, optimisationMode.getValue());
    }
}
