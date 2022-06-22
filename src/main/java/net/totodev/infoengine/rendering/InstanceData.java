package net.totodev.infoengine.rendering;

import net.totodev.infoengine.util.IBufferWritable;
import org.joml.*;

import java.nio.ByteBuffer;

public class InstanceData implements IBufferWritable {
    public Vector2f size;
    public Matrix4f modelMatrix;

    public InstanceData(Vector2f size, Matrix4f modelMatrix) {
        this.size = size;
        this.modelMatrix = modelMatrix;
    }

    @Override
    public int bytes() {
        return 2 * Float.BYTES + 16 * Float.BYTES;
    }
    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        size.get(buffer);
        modelMatrix.get(buffer);
    }
}
