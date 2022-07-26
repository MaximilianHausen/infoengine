package net.totodev.engine.core.components;

import net.totodev.engine.ecs.Component;
import net.totodev.engine.resources.scene.ComponentDataModel;
import net.totodev.engine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;
import org.joml.*;

//TODO: Remake similar to Transform2d
//TODO: Parents, Local/Parent(World)/World(Recursive)

/**
 * CURRENTLY BROKEN
 */
public class Transform3d implements Component {
    private final MutableIntObjectMap<Matrix4f> transforms = IntObjectMaps.mutable.empty();

    /**
     * Gets the position of an entity and stores it in the out-vector.
     * @param entityId The entity to get the position of
     * @param out      The vector to store the position in
     * @return out, or null, if this component is not present on that entity
     */
    public Vector3f getPosition(int entityId, @NotNull Vector3f out) {
        if (!isPresentOn(entityId)) return null;
        return transforms.get(entityId).getTranslation(out);
    }
    public void setPosition(int entityId, float xPos, float yPos, float zPos) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).setTranslation(xPos, yPos, zPos);
    }
    public void setPosition(int entityId, @NotNull Vector3f pos) {
        setPosition(entityId, pos.x, pos.y, pos.z);
    }
    public void move(int entityId, float xOffset, float yOffset, float zOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translate(xOffset, yOffset, zOffset);
    }
    public void move(int entityId, @NotNull Vector3f posOffset) {
        move(entityId, posOffset.x, posOffset.y, posOffset.z);
    }

    public Quaternionf getRotation(int entityId, @NotNull Quaternionf out) {
        if (!isPresentOn(entityId)) return null;
        return transforms.get(entityId).getUnnormalizedRotation(out);
    }
    public void setRotation(int entityId, float angleX, float angleY, float angleZ) {
        setRotation(entityId, new Quaternionf().rotateXYZ(angleX, angleY, angleZ));
    }
    public void setRotation(int entityId, @NotNull Quaternionf rot) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                rot,
                getScale(entityId, new Vector3f()));
    }
    public void rotate(int entityId, float angleOffsetX, float angleOffsetY, float angleOffsetZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).rotateXYZ(angleOffsetX, angleOffsetY, angleOffsetZ);
    }
    public void rotate(int entityId, @NotNull Quaternionf rotOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).rotate(rotOffset);
    }

    /**
     * Gets the scale of an entity on all three axes and stores it in the out-vector.
     * @param entityId The entity to get the scale of
     * @param out      The vector to store the rotation in
     * @return out, or null, if this component is not present on that entity
     */
    public Vector3f getScale(int entityId, @NotNull Vector3f out) {
        if (!isPresentOn(entityId)) return null;
        return transforms.get(entityId).getScale(out);
    }
    public void setScale(int entityId, float scaleX, float scaleY, float scaleZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                getRotation(entityId, new Quaternionf()),
                new Vector3f(scaleX, scaleY, scaleZ));
    }
    public void setScale(int entityId, @NotNull Vector3f scale) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                getRotation(entityId, new Quaternionf()),
                scale);
    }
    // Multiplier, not offset
    public void scale(int entityId, float scaleX, float scaleY, float scaleZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).scale(scaleX, scaleY, scaleZ);
    }
    public void scale(int entityId, @NotNull Vector3f scale) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).scale(scale);
    }

    @Override
    public void resetEntity(int entityId) {
        transforms.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        transforms.get(data.entity).set(SerializationUtils.deserialize(data.value));
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return SerializationUtils.serialize(transforms.get(entityId).get(new float[16]));
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return transforms.containsKey(entityId);
    }
}
