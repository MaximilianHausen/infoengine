package net.totodev.infoengine.ecs;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform3d implements IComponent {
    private final MutableIntObjectMap<Matrix4f> transforms = IntObjectMaps.mutable.empty();
    private final Scene scene;

    public Transform3d(Scene scene) {
        this.scene = scene;
        scene.events.subscribe("EntityCreated", Integer.class, (i) -> transforms.put(i, new Matrix4f()));
        scene.events.subscribe("EntityDestroyed", Integer.class, transforms::remove);
        //TODO: Unregister
    }

    public @Nullable Vector3f getPosition(int entityId, @NotNull Vector3f out) {
        return transforms.get(entityId).getTranslation(out);
    }
    public void setPosition(int entityId, float xPos, float yPos, float zPos) {
        transforms.get(entityId).setTranslation(xPos, yPos, zPos);
    }
    public void setPosition(int entityId, @NotNull Vector3f pos) {
        transforms.get(entityId).setTranslation(pos);
    }
    public void move(int entityId, float xOffset, float yOffset, float zOffset) {
        transforms.get(entityId).translate(xOffset, yOffset, zOffset);
    }
    public void move(int entityId, @NotNull Vector3f posOffset) {
        transforms.get(entityId).translate(posOffset);
    }

    public Quaternionf getRotation(int entityId, @NotNull Quaternionf out) {
        return transforms.get(entityId).getUnnormalizedRotation(out);
    }
    public void setRotation(int entityId, float angleX, float angleY, float angleZ) {
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                new Quaternionf().rotateXYZ(angleX, angleY, angleZ),
                getScale(entityId, new Vector3f()));
    }
    public void setRotation(int entityId, @NotNull Quaternionf rot) {
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                rot,
                getScale(entityId, new Vector3f()));
    }
    public void rotate(int entityId, float angleOffsetX, float angleOffsetY, float angleOffsetZ) {
        transforms.get(entityId).rotateXYZ(angleOffsetX, angleOffsetY, angleOffsetZ);
    }
    public void rotate(int entityId, @NotNull Quaternionf rotOffset) {
        transforms.get(entityId).rotate(rotOffset);
    }

    public Vector3f getScale(int entityId, @NotNull Vector3f out) {
        return transforms.get(entityId).getScale(out);
    }
    public void setScale(int entityId, float scaleX, float scaleY, float scaleZ) {
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                getRotation(entityId, new Quaternionf()),
                new Vector3f(scaleX, scaleY, scaleZ));
    }
    public void setScale(int entityId, @NotNull Vector3f scale) {
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                getRotation(entityId, new Quaternionf()),
                scale);
    }
    // Multiplier, not offset
    public void scale(int entityId, float scaleX, float scaleY, float scaleZ) {
        transforms.get(entityId).scale(scaleX, scaleY, scaleZ);
    }
    public void scale(int entityId, @NotNull Vector3f scale) {
        transforms.get(entityId).scale(scale);
    }
    
    public Scene getScene() {
        return scene;
    }
    public boolean isPresentOn(int entityId) {
        return transforms.containsKey(entityId);
    }
    public boolean allowsMultiple() {
        return false;
    }
}
