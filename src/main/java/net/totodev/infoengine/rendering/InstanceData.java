package net.totodev.infoengine.rendering;

import net.totodev.infoengine.util.BufferWritable;
import org.joml.*;

import java.nio.ByteBuffer;

public class InstanceData implements BufferWritable {
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
        return Integer.BYTES + 2 * Float.BYTES + 16 * Float.BYTES;
    }
    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(imageIndex);
        size.get(buffer);
        modelMatrix.get(buffer);
    }
}
