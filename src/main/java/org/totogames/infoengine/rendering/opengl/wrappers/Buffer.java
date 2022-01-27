package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.totogames.infoengine.DisposedException;
import org.totogames.infoengine.rendering.opengl.enums.BufferAccessRestrictions;
import org.totogames.infoengine.rendering.opengl.enums.BufferBindTargets;
import org.totogames.infoengine.rendering.opengl.enums.BufferUsage;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

// TODO: Clearing + Mapping + Streaming + BindRange + GetParameters

/**
 * Java wrapper for OpenGL buffer objects
 *
 * @see <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">OpenGL Wiki</a>
 */
public class Buffer implements IOglObject {
    private final int id;
    private boolean isDisposed = false;
    @Nullable
    private BufferBindTargets currentBindTarget;

    public Buffer() {
        id = glGenBuffers();
        Logger.log(LogSeverity.Debug, "OpenGL", "Buffer created with id " + id);
    }

    public void bind(BufferBindTargets target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glBindBuffer(target.getValue(), id);
        currentBindTarget = target;

        Logger.log(LogSeverity.Trace, "OpenGL", "Buffer " + id + " bound to target " + target);
    }
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget != null) {
            BufferBindTargets temp = currentBindTarget;

            glBindBuffer(currentBindTarget.getValue(), 0);
            currentBindTarget = null;

            Logger.log(LogSeverity.Trace, "OpenGL", "Buffer " + id + " unbound from target " + temp);
        }
    }

    public BufferBindTargets getCurrentBindTarget() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        return currentBindTarget;
    }

    //region GetPartialData
    @RequiresBind
    public void getPartialData(long offset, @NotNull ByteBuffer target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull ShortBuffer target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull IntBuffer target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull LongBuffer target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull FloatBuffer target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull DoubleBuffer target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull short[] target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull int[] target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull long[] target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull float[] target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    @RequiresBind
    public void getPartialData(long offset, @NotNull double[] target) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glGetBufferSubData(currentBindTarget.getValue(), offset, target);
    }
    //endregion

    //region SetData
    @RequiresBind
    public void setData(@Range(from = 0, to = Long.MAX_VALUE) long size, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), size, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull ByteBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull ShortBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull IntBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull LongBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull FloatBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull DoubleBuffer data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull short[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull int[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull long[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull float[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    @RequiresBind
    public void setData(@NotNull double[] data, @NotNull BufferUsage optimisationMode) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferData(currentBindTarget.getValue(), data, optimisationMode.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Data set for buffer " + id);
    }
    //endregion

    //region SetImmutableData
    // For some reason there is no LongBuffer variant
    @RequiresBind
    public void setImmutableData(@Range(from = 0, to = Long.MAX_VALUE) long size, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), size, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull ByteBuffer data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull ShortBuffer data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull IntBuffer data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull FloatBuffer data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull DoubleBuffer data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull short[] data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull int[] data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull float[] data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    @RequiresBind
    public void setImmutableData(@NotNull double[] data, @NotNull BufferAccessRestrictions... flags) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferStorage(currentBindTarget.getValue(), data, BufferAccessRestrictions.combineFlags(flags));
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable data set for buffer " + id);
    }
    //endregion

    //region SetPartialData
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull ByteBuffer data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull ShortBuffer data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull IntBuffer data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull LongBuffer data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull FloatBuffer data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull DoubleBuffer data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull short[] data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull int[] data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull float[] data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull long[] data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    @RequiresBind
    public void setPartialData(@Range(from = 0, to = Long.MAX_VALUE) long offset, @NotNull double[] data) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        if (currentBindTarget == null) throw new BufferNotBoundException();
        glBufferSubData(currentBindTarget.getValue(), offset, data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for buffer " + id + " with offset " + offset);
    }
    //endregion

    public void copy(Buffer target, @Range(from = 0, to = Long.MAX_VALUE) long readOffset, @Range(from = 0, to = Long.MAX_VALUE) long writeOffset, @Range(from = 0, to = Long.MAX_VALUE) long size) {
        if (target == this) throw new UnsupportedOperationException("Copying to the same buffer is not yet supported");
        BufferBindTargets temp1 = currentBindTarget;
        BufferBindTargets temp2 = target.currentBindTarget;

        bind(BufferBindTargets.COPY_READ_BUFFER);
        target.bind(BufferBindTargets.COPY_WRITE_BUFFER);

        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);

        if (temp1 != null) bind(temp1);
        else unbind();
        if (temp2 != null) target.bind(temp2);
        else target.unbind();

        Logger.log(LogSeverity.Trace, "OpenGL", "Data copied from buffer " + id + " with offset " + readOffset + " to buffer " + id + " with offset " + writeOffset);
    }

    public void reset() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glInvalidateBufferData(id);
        Logger.log(LogSeverity.Trace, "OpenGL", "Data invalidated/reset for buffer " + id);
    }
    public void partialReset(@Range(from = 0, to = Long.MAX_VALUE) long offset, @Range(from = 0, to = Long.MAX_VALUE) long length) {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glInvalidateBufferSubData(id, offset, length);
        Logger.log(LogSeverity.Trace, "OpenGL", "Data [" + offset + ".." + (offset + length) + "] invalidated/reset for buffer " + id);
    }

    public int getId() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new DisposedException("Buffer was already disposed");
        glDeleteBuffers(id);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "OpenGL", "Buffer deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
