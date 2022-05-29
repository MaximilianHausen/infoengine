package net.totodev.infoengine.rendering;

import net.totodev.infoengine.util.IBufferWritable;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public class MatrixUbo implements IBufferWritable {
    public Matrix4f model = new Matrix4f();
    public Matrix4f view = new Matrix4f();
    public Matrix4f proj = new Matrix4f();

    public int bytes() {
        return 3 * 16 * Float.BYTES;
    }

    public void writeToBuffer(ByteBuffer buffer) {
        final int mat4Size = 16 * Float.BYTES;

        model.get(0, buffer);
        view.get(mat4Size, buffer);
        proj.get(mat4Size * 2, buffer);
    }
}
