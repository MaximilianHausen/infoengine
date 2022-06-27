package net.totodev.infoengine.core.components;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import net.totodev.infoengine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;
import org.joml.*;

public class Transform2d implements Component {
    private final MutableIntObjectMap<Matrix3x2f> transforms = IntObjectMaps.mutable.empty();

    public Vector2f getPosition(int entityId, @NotNull Vector2f out) {
        if (!isPresentOn(entityId)) return null;
        Matrix3x2f transform = transforms.get(entityId);
        return out.set(transform.m20, transform.m21);
    }
    public void setPosition(int entityId, float xPos, float yPos) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).setTranslation(xPos, yPos);
    }
    public void setPosition(int entityId, @NotNull Vector2f pos) {
        setPosition(entityId, pos.x, pos.y);
    }
    public void move(int entityId, float xOffset, float yOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.put(entityId, transforms.get(entityId).translate(xOffset, yOffset));
    }
    public void move(int entityId, @NotNull Vector2f posOffset) {
        move(entityId, posOffset.x, posOffset.y);
    }

    /**
     * POSITIVE_INFINITY if not present on that entity
     */
    public float getRotation(int entityId) {
        if (!isPresentOn(entityId)) return Float.POSITIVE_INFINITY;
        return transforms.get(entityId).positiveX(new Vector2f()).angle(new Vector2f(1, 0));
    }
    public void setRotation(int entityId, float angle) {
        if (!isPresentOn(entityId)) return;
        Vector2f oldPosition = getPosition(entityId, new Vector2f());
        Vector2f oldScale = getScale(entityId, new Vector2f());
        Matrix3x2f transform = transforms.get(entityId);
        transform.translation(oldPosition);
        transform.rotate(angle);
        transform.scale(oldScale);
    }
    public void rotate(int entityId, float angleOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).rotate(angleOffset);
    }

    public Vector2f getScale(int entityId, @NotNull Vector2f out) {
        if (!isPresentOn(entityId)) return null;
        Matrix3x2f transform = transforms.get(entityId);
        return out.set(transform.normalizedPositiveX(out).length(), transform.normalizedPositiveY(out).length());
    }
    public void setScale(int entityId, float scaleX, float scaleY) {
        if (!isPresentOn(entityId)) return;
        Vector2f oldPosition = getPosition(entityId, new Vector2f());
        float oldRotation = getRotation(entityId);
        Matrix3x2f transform = transforms.get(entityId);
        transform.translation(oldPosition);
        transform.rotate(oldRotation);
        transform.scale(scaleX, scaleY);
    }
    public void setScale(int entityId, @NotNull Vector2f scale) {
        setScale(entityId, scale.x, scale.y);
    }
    // Multiplier, not offset
    public void scale(int entityId, float scaleX, float scaleY) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).scale(scaleX, scaleY);
    }
    public void scale(int entityId, @NotNull Vector2f scale) {
        scale(entityId, scale.x, scale.y);
    }

    //region IComponent
    @Override
    public void addOnEntity(int entityId) {
        transforms.put(entityId, new Matrix3x2f());
    }
    @Override
    public void removeFromEntity(int entityId) {
        transforms.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        if (!isPresentOn(data.entity))
            addOnEntity(data.entity);

        transforms.get(data.entity).set(SerializationUtils.deserialize(data.value));
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return SerializationUtils.serialize(transforms.get(entityId).get(new float[6]));
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return transforms.containsKey(entityId);
    }
    //endregion
}
