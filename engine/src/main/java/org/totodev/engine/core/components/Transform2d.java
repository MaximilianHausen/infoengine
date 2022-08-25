package org.totodev.engine.core.components;

import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;
import org.totodev.engine.ecs.Component;
import org.totodev.engine.resources.scene.ComponentDataModel;
import org.totodev.engine.util.SerializationUtils;

public class Transform2d implements Component {
    private final MutableIntObjectMap<Vector2f> positions = IntObjectMaps.mutable.empty();
    private final MutableIntFloatMap rotations = IntFloatMaps.mutable.empty();
    private final MutableIntObjectMap<Vector2f> scales = IntObjectMaps.mutable.empty();

    //region Position
    public Vector2f getPosition(int entityId, @NotNull Vector2f out) {
        Vector2f pos = positions.get(entityId);
        if (pos == null) return null;
        return out.set(pos);
    }

    public void setPosition(int entityId, @NotNull Vector2f pos) {
        positions.put(entityId, pos);
    }
    public void setPosition(int entityId, float x, float y) {
        positions.put(entityId, new Vector2f(x, y));
    }

    public void move(int entityId, @NotNull Vector2f posOffset) {
        Vector2f pos = positions.get(entityId);
        if (pos == null) return;
        pos.add(posOffset);
    }
    public void move(int entityId, float xOffset, float yOffset) {
        Vector2f pos = positions.get(entityId);
        if (pos == null) return;
        pos.add(xOffset, yOffset);
    }
    //endregion

    //region Rotation
    public float getRotation(int entityId) {
        return rotations.get(entityId);
    }

    public void setRotation(int entityId, float angle) {
        rotations.put(entityId, angle);
    }

    public void rotate(int entityId, float angleOffset) {
        if (!rotations.containsKey(entityId)) return;
        rotations.addToValue(entityId, angleOffset);
    }
    //endregion

    //region Scale
    public Vector2f getScale(int entityId, @NotNull Vector2f out) {
        Vector2f scale = scales.get(entityId);
        if (scale == null) return null;
        return out.set(scale);
    }

    public void setScale(int entityId, @NotNull Vector2f scale) {
        scales.put(entityId, scale);
    }
    public void setScale(int entityId, float x, float y) {
        scales.put(entityId, new Vector2f(x, y));
    }

    // Multiplier, not offset
    public void scale(int entityId, @NotNull Vector2f scaleMul) {
        Vector2f scale = scales.get(entityId);
        if (scale == null) return;
        scale.mul(scaleMul);
    }
    public void scale(int entityId, float xMul, float yMul) {
        Vector2f scale = scales.get(entityId);
        if (scale == null) return;
        scale.mul(xMul, yMul);
    }
    public void scale(int entityId, float scaleMul) {
        Vector2f scale = scales.get(entityId);
        if (scale == null) return;
        scale.mul(scaleMul);
    }
    //endregion

    @Override
    public void resetEntity(int entityId) {
        positions.remove(entityId);
        rotations.remove(entityId);
        scales.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        float[] values = SerializationUtils.deserialize(data.value);
        setPosition(data.entity, values[0], values[1]);
        setRotation(data.entity, values[2]);
        setScale(data.entity, values[3], values[4]);
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f pos = positions.get(entityId);
        Vector2f scale = scales.get(entityId);
        return SerializationUtils.serialize(pos.x, pos.y, rotations.get(entityId), scale.x, scale.y);
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return positions.containsKey(entityId) && rotations.containsKey(entityId) && scales.containsKey(entityId);
    }
}
