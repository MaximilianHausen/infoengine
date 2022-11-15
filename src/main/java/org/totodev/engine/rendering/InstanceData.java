package org.totodev.engine.rendering;

import org.joml.*;
import org.totodev.engine.util.BufferWritable;

import java.nio.ByteBuffer;

public class InstanceData implements BufferWritable {
    public static final int BYTES = Integer.BYTES + 2 * Float.BYTES + 16 * Float.BYTES;

    public int imageIndex;
    public Vector2f size;
    public Matrix4f modelMatrix;

    public InstanceData(int imageIndex, Vector2f size, Matrix4f modelMatrix) {
        this.imageIndex = imageIndex;
        this.size = size;
        this.modelMatrix = modelMatrix;
    }

    @Override
    public int bytes() {
        return BYTES;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer, int offset) {
        buffer.putInt(offset, imageIndex);
        size.get(offset + Integer.BYTES, buffer);
        modelMatrix.get(offset + Integer.BYTES + 2 * Float.BYTES, buffer);
    }
}
