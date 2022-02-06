package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.totogames.infoengine.rendering.opengl.enums.BufferBindTarget;
import org.totogames.infoengine.rendering.opengl.enums.VertexAttribDataType;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

//TODO: Cache current attributes and autocalculate stride
public class VertexArray implements IOglObject {
    private static VertexArray currentBound;
    private final int id;
    private boolean isDisposed = false;

    public VertexArray() {
        id = glGenVertexArrays();
        Logger.log(LogSeverity.Debug, "OpenGL", "VertexArray created with id " + id);
    }

    public void bind() {
        if (isDisposed) throw new VertexArrayDisposedException();
        glBindVertexArray(id);
        currentBound = this;
        Logger.log(LogSeverity.Trace, "OpenGL", "VertexArray " + id + " bound");
    }
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new VertexArrayDisposedException();
        if (currentBound == this) {
            glBindVertexArray(id);
            currentBound = null;
            Logger.log(LogSeverity.Trace, "OpenGL", "VertexArray " + id + " unbound");
        }
    }

    /**
     * @param vertexBuffer The buffer to set as the attribute
     * @param index        The vertex attrib to set
     * @param size         Number of values per vertex
     * @param type         Type of the values
     * @param stride       Bytes per vertex, must be the same for all attributes
     */
    @RequiresBind
    public void setVertexAttribute(@NotNull Buffer vertexBuffer, int index, @Range(from = 1, to = 4) int size, VertexAttribDataType type, int stride) {
        if (isDisposed) throw new VertexArrayDisposedException();
        vertexBuffer.bind(BufferBindTarget.ARRAY_BUFFER);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type.getValue(), false, stride, 0);
    }
    @RequiresBind
    public void setElementBuffer(@NotNull Buffer elementBuffer) {
        if (isDisposed) throw new VertexArrayDisposedException();
        elementBuffer.bind(BufferBindTarget.ELEMENT_ARRAY_BUFFER);
    }

    public int getId() {
        if (isDisposed) throw new VertexArrayDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new VertexArrayDisposedException();
        glDeleteVertexArrays(id);
        isDisposed = true;
        if (currentBound == this) currentBound = null;
        Logger.log(LogSeverity.Debug, "OpenGL", "VertexArray deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
