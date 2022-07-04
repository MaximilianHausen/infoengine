package net.totodev.infoengine.physics;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import net.totodev.infoengine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;

public class Velocity2d implements Component {
    private final MutableIntObjectMap<Vector2f> velocities = IntObjectMaps.mutable.empty();
    private final MutableIntFloatMap rotVelocities = IntFloatMaps.mutable.empty();

    public Vector2f getVelocity(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return velocities.get(entityId);
    }
    public void setVelocity(int entityId, Vector2f velocity) {
        if (!isPresentOn(entityId)) return;
        velocities.get(entityId).set(velocity);
    }
    public void setVelocity(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        velocities.get(entityId).set(x, y);
    }
    public void changeVelocity(int entityId, Vector2f velocity) {
        if (!isPresentOn(entityId)) return;
        velocities.get(entityId).add(velocity);
    }
    public void changeVelocity(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        velocities.get(entityId).add(x, y);
    }

    public float getRotVelocity(int entityId) {
        if (!isPresentOn(entityId)) return 0;
        return rotVelocities.get(entityId);
    }
    public void setRotVelocity(int entityId, float rotVelocity) {
        if (!isPresentOn(entityId)) return;
        rotVelocities.put(entityId, rotVelocity);
    }
    public void changeRotVelocity(int entityId, float rotVelocity) {
        if (!isPresentOn(entityId)) return;
        rotVelocities.put(entityId, rotVelocities.get(entityId) + rotVelocity);
    }

    @Override
    public void addOnEntity(int entityId) {
        velocities.put(entityId, new Vector2f());
    }
    @Override
    public void removeFromEntity(int entityId) {
        velocities.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        if (!isPresentOn(data.entity))
            addOnEntity(data.entity);
        float[] values = SerializationUtils.deserialize(data.value);
        velocities.get(data.entity).set(values[0], values[1]);
        rotVelocities.put(data.entity, values[2]);
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f vel = velocities.get(entityId);
        return SerializationUtils.serialize(vel.x, vel.y, rotVelocities.get(entityId));
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return velocities.containsKey(entityId);
    }
}
