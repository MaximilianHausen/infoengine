package org.totodev.engine.physics;

import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;
import org.totodev.engine.ecs.Component;
import org.totodev.engine.resources.scene.ComponentDataModel;
import org.totodev.engine.util.SerializationUtils;

public class Velocity2d implements Component {
    private final MutableIntObjectMap<Vector2f> velocities = IntObjectMaps.mutable.empty();
    private final MutableIntFloatMap rotVelocities = IntFloatMaps.mutable.empty();

    //region Velocity
    public Vector2f getVelocity(int entityId, @NotNull Vector2f out) {
        Vector2f vel = velocities.get(entityId);
        if (vel == null) return null;
        return out.set(vel);
    }

    public void setVelocity(int entityId, @NotNull Vector2f velocity) {
        velocities.put(entityId, velocity);
    }
    public void setVelocity(int entityId, float x, float y) {
        velocities.put(entityId, new Vector2f(x, y));
    }

    public void changeVelocity(int entityId, @NotNull Vector2f velChange) {
        Vector2f vel = velocities.get(entityId);
        if (vel == null) return;
        vel.add(velChange);
    }
    public void changeVelocity(int entityId, float x, float y) {
        Vector2f vel = velocities.get(entityId);
        if (vel == null) return;
        vel.add(x, y);
    }
    //endregion

    //region RotVelocity
    public float getRotVelocity(int entityId) {
        return rotVelocities.get(entityId);
    }

    public void setRotVelocity(int entityId, float rotVelocity) {
        rotVelocities.put(entityId, rotVelocity);
    }

    public void changeRotVelocity(int entityId, float rotVelChange) {
        if (!rotVelocities.containsKey(entityId)) return;
        rotVelocities.addToValue(entityId, rotVelChange);
    }
    //endregion

    @Override
    public void resetEntity(int entityId) {
        velocities.remove(entityId);
        rotVelocities.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        float[] values = SerializationUtils.deserialize(data.value);
        setVelocity(data.entity, values[0], values[1]);
        setRotVelocity(data.entity, values[2]);
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f vel = velocities.get(entityId);
        return SerializationUtils.serialize(vel.x, vel.y, rotVelocities.get(entityId));
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return velocities.containsKey(entityId) && rotVelocities.containsKey(entityId);
    }
}
