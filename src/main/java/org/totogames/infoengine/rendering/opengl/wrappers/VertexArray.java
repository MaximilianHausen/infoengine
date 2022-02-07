package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.rendering.opengl.enums.BufferBindTarget;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

//TODO: Cache current attributes and autocalculate stride
public class VertexArray implements IOglObject {
    private static VertexArray currentBound;
    private final int id;
    private boolean isDisposed = false;
    private int activatedAttributes = 0;

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

        // Calculate stride
        int stride = 0;
        for (var attribute : attributes)
            stride += attribute.getByteSize();

        // Set attribute pointers
        for (int i = 0; i < attributes.length; i++) {
            VertexAttribute attribute = attributes[i];
            attribute.getVertexBuffer().bind(BufferBindTarget.ARRAY_BUFFER);
            glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getValue(), false, stride, 0);
        }

        activatedAttributes = attributes.length;
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
