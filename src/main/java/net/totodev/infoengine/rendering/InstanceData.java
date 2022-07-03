package net.totodev.infoengine.rendering;

import net.totodev.infoengine.util.BufferWritable;
import org.joml.*;

import java.nio.ByteBuffer;

public class InstanceData implements BufferWritable {
    public static final int BYTES = 2 * Float.BYTES + 16 * Float.BYTES + Integer.BYTES;

    public Vector2f size;
    public Matrix4f modelMatrix;
    public int imageIndex;

    public InstanceData(Vector2f size, Matrix4f modelMatrix, int imageIndex) {
        this.size = size;
        this.modelMatrix = modelMatrix;
        this.imageIndex = imageIndex;
    }

    @Override
    public int bytes() {
        return BYTES;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer, int offset) {
        size.get(offset, buffer);
        modelMatrix.get(offset + 2 * Float.BYTES, buffer);
        buffer.putInt(offset + 2 * Float.BYTES + 16 * Float.BYTES, imageIndex);
    }
}
