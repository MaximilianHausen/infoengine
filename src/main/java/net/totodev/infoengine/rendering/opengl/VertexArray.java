package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.opengl.enums.BufferBindTarget;
import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL46C.*;

/**
 * Java wrapper for OpenGL vertex array objects
 * @see <a href="https://www.khronos.org/opengl/wiki/Vertex_Specification#Vertex_Array_Object">OpenGL Wiki: VertexArray</a>
 */
public class VertexArray implements IOglObject {
    private static VertexArray currentBound;
    private final int id;
    private boolean isDisposed = false;
    private int activatedAttributes = 0;

    public VertexArray() {
        id = glGenVertexArrays();
        Logger.log(LogSeverity.Debug, "OpenGL", "VertexArray created with id " + id);
    }

    /**
     * Binds this vertex array.
     */
    public void bind() {
        if (isDisposed) throw new VertexArrayDisposedException();
        glBindVertexArray(id);
        currentBound = this;
        Logger.log(LogSeverity.Trace, "OpenGL", "VertexArray " + id + " bound");
    }
    /**
     * Unbinds this vertex array
     */
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new VertexArrayDisposedException();
        if (currentBound == this) {
            glBindVertexArray(0);
            currentBound = null;
            Logger.log(LogSeverity.Trace, "OpenGL", "VertexArray " + id + " unbound");
        }
    }

    /**
     * Sets the vertex attributes. Stride, offset and enabling/disabling is handled automatically
     * @param attributes The vertex attributes to set
     */
    @RequiresBind
    public void setVertexAttributes(VertexAttribute... attributes) {
        if (isDisposed) throw new VertexArrayDisposedException();

        // Enable/Disable vertex attributes
        if (attributes.length < activatedAttributes)
            for (int i = attributes.length - 1; i < activatedAttributes; i++)
                glDisableVertexAttribArray(i);
        else if (attributes.length > activatedAttributes)
            for (int i = activatedAttributes; i < attributes.length; i++)
                glEnableVertexAttribArray(i);

        // Calculate stride + offsets
        int stride = 0;
        int[] offsets = new int[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            VertexAttribute attribute = attributes[i];
            stride += attribute.getByteSize();
            if (i < attributes.length - 1)
                offsets[i + 1] = offsets[i] + attribute.getByteSize();
        }

        // Set attribute pointers
        for (int i = 0; i < attributes.length; i++) {
            VertexAttribute attribute = attributes[i];
            attribute.vertexBuffer().bind(BufferBindTarget.ARRAY_BUFFER);
            glVertexAttribPointer(i, attribute.size(), attribute.type().getValue(), false, stride, offsets[i]);
        }

        activatedAttributes = attributes.length;
    }

    /**
     * Sets the element buffer. This is equivalent to binding it on ELEMENT_ARRAY_BUFFER
     * @param elementBuffer The element buffer to set
     */
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
