package net.totodev.infoengine.rendering.opengl;

import com.google.common.collect.HashBiMap;
import net.totodev.infoengine.rendering.opengl.enums.BufferAccessRestriction;
import net.totodev.infoengine.rendering.opengl.enums.BufferBindTarget;
import net.totodev.infoengine.rendering.opengl.enums.BufferUsage;
import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

// TODO: Clearing + Mapping + Streaming + BindRange + GetParameters

/**
 * Java wrapper for OpenGL buffer objects
 * @see <a href="https://khronos.org/opengl/wiki/Buffer_Object">OpenGL Wiki: Buffers</a>
 */
public class Buffer implements IOglObject {
    private final static HashBiMap<Buffer, BufferBindTarget> bindStatus = HashBiMap.create();
    private final int id;
    private boolean isDisposed = false;

    public Buffer() {
        id = glGenBuffers();
        Logger.log(LogSeverity.Debug, "OpenGL", "Buffer created with id " + id);
    }

    /**
     * Gets the currently bound buffer
     * @param target The target to get the buffer from
     * @return The buffer, or null if nothing is bound on that target
     */
    public static @Nullable Buffer getBoundBuffer(@NotNull BufferBindTarget target) {
        return bindStatus.inverse().get(target);
    }

    /**
     * Binds this buffer to a buffer bind target.
     * @param target The target to bind to
     */
    public void bind(BufferBindTarget target) {
        if (isDisposed) throw new BufferDisposedException();
        glBindBuffer(target.getValue(), id);
        bindStatus.forcePut(this, target);

        Logger.log(LogSeverity.Trace, "OpenGL", "Buffer " + id + " bound to target " + target);
    }

    /**
     * Unbinds this buffer from the targets it is currently bound to
     */
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new BufferDisposedException();
        if (bindStatus.containsKey(this)) {
            BufferBindTarget target = bindStatus.get(this);
            glBindBuffer(target.getValue(), 0);
            bindStatus.remove(this);
            Logger.log(LogSeverity.Trace, "OpenGL", "Buffer " + id + " unbound from target " + target);
        }
    }

    public @Nullable BufferBindTarget getBindStatus() {
        if (isDisposed) throw new BufferDisposedException();
        return bindStatus.get(this);
    }

    // Not doing javadoc for these, it would be a stupid amount of copying and pasting
    //region GetPartialData
    @RequiresBind
    public void getPartialData(long offset, @NotNull ByteBuffer target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull ShortBuffer target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull IntBuffer target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull LongBuffer target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull FloatBuffer target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull DoubleBuffer target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull short[] target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull int[] target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull long[] target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull float[] target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull double[] target) {
        if (isDisposed) throw new BufferDisposedException();
        glGetBufferSubData(bindStatus.get(this).getValue(), offset, target);
    }
    //endregion

    //region SetData
    @RequiresBind
    public void setData(@NotNull ByteBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull ShortBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull IntBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull LongBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull FloatBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull DoubleBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull short[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull int[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull long[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull float[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull double[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferData(bindStatus.get(this).getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    //endregion

    //region SetImmutableData
    // For some reason there is no LongBuffer variant
    @RequiresBind
    public void setImmutableData(@NotNull ByteBuffer data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull ShortBuffer data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull IntBuffer data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull FloatBuffer data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull DoubleBuffer data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull short[] data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull int[] data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull float[] data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull double[] data, @NotNull BufferAccessRestriction... flags) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferStorage(bindStatus.get(this).getValue(), data, BufferAccessRestriction.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    //endregion

    //region SetPartialData
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull ShortBuffer data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull IntBuffer data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull LongBuffer data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull FloatBuffer data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull DoubleBuffer data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull short[] data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull int[] data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull float[] data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull long[] data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull double[] data) {
        if (isDisposed) throw new BufferDisposedException();
        glBufferSubData(bindStatus.get(this).getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    //endregion

    /**
     * Copies data from this buffer to another
     * @param target      The buffer to write the data to
     * @param readOffset  The position in this buffer to start reading
     * @param writeOffset The position in the receiving buffer to start writing
     * @param size        The amount of data to copy, in bytes
     */
    public void copy(Buffer target, @Range(from = 0, to = Long.MAX_VALUE) long readOffset, @Range(from = 0, to = Long.MAX_VALUE) long writeOffset, @Range(from = 0, to = Long.MAX_VALUE) long size) {
        if (target == this) throw new UnsupportedOperationException("Copying to the same buffer is not yet supported");
        BufferBindTarget temp1 = bindStatus.get(this);
        BufferBindTarget temp2 = bindStatus.get(target);

        bind(BufferBindTarget.COPY_READ_BUFFER);
        target.bind(BufferBindTarget.COPY_WRITE_BUFFER);
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);

        if (temp1 != null) bind(temp1);
        else unbind();
        if (temp2 != null) target.bind(temp2);
        else target.unbind();

        Logger.log(LogSeverity.Trace, "OpenGL", size + " bytes copied from buffer " + id + " with offset " + readOffset + " to buffer " + id + " with offset " + writeOffset);
    }

    /**
     * Completely resets/invalidates this buffer
     */
    public void reset() {
        if (isDisposed) throw new BufferDisposedException();
        glInvalidateBufferData(id);
        Logger.log(LogSeverity.Trace, "OpenGL", "Data invalidated/reset for buffer " + id);
    }
    /**
     * Reset/invalidate a part of this buffer
     * @param offset The position in this buffer to start resetting
     * @param length The amount of data to reset, in bytes
     */
    public void partialReset(@Range(from = 0, to = Long.MAX_VALUE) long offset, @Range(from = 0, to = Long.MAX_VALUE) long length) {
        if (isDisposed) throw new BufferDisposedException();
        glInvalidateBufferSubData(id, offset, length);
        Logger.log(LogSeverity.Trace, "OpenGL", "Data [" + offset + ".." + (offset + length) + "] invalidated/reset for buffer " + id);
    }

    public int getId() {
        if (isDisposed) throw new BufferDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new BufferDisposedException();
        glDeleteBuffers(id);
        bindStatus.remove(this);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "OpenGL", "Buffer deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}