package net.totodev.infoengine.rendering;

import net.totodev.infoengine.util.IBufferWritable;
import org.joml.*;

import java.nio.ByteBuffer;

public class InstanceData implements IBufferWritable {
    public Vector2f size;
    public int textureIndex;
    public Matrix4f modelMatrix;

    public InstanceData(Vector2f size, int textureIndex, Matrix4f modelMatrix) {
        this.size = size;
        this.textureIndex = textureIndex;
        this.modelMatrix = modelMatrix;
    }

    @Override
    public int bytes() {
        return 2 * Float.BYTES + Integer.BYTES + 16 * Float.BYTES;
    }
    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        size.get(buffer);
        buffer.putInt(textureIndex);
        modelMatrix.get(buffer);
    }
}
