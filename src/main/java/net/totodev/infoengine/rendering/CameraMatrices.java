package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.components.Transform2d;
import net.totodev.infoengine.util.BufferWritable;
import org.joml.*;

import java.nio.ByteBuffer;

public class CameraMatrices implements BufferWritable {
    public Matrix4f view;
    public Matrix4f proj;

    public CameraMatrices(Matrix4f view, Matrix4f proj) {
        this.view = view;
        this.proj = proj;
    }

    public static CameraMatrices fromCamera(Camera2d camera, Transform2d transform, int entityId) {
        Vector2f size = camera.getSize(entityId, new Vector2f());

        Vector2f pos = transform.getPosition(entityId, new Vector2f()).add(camera.getOffset(entityId, new Vector2f()));
        float rot = transform.getRotation(entityId);
        Vector2f scale = transform.getScale(entityId, new Vector2f());

        return new CameraMatrices(
                new Matrix4f().translationRotateScaleInvert(new Vector3f(pos.x, pos.y, 0), new Quaternionf().rotateZ(rot), new Vector3f(scale.x, scale.y, 1)),
                new Matrix4f()//.setOrtho2D(size.x / 2, size.x / 2, size.y / 2, size.y / 2) FIXME
        );
    }

    @Override
    public int bytes() {
        return 2 * 16 * Float.BYTES;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        view.get(0, buffer);
        proj.get(16 * Float.BYTES, buffer);
    }
}
