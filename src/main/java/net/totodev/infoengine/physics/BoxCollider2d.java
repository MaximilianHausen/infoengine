package net.totodev.infoengine.physics;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import net.totodev.infoengine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;

public class BoxCollider2d implements Component {
    public MutableIntObjectMap<Vector2f> sizes = IntObjectMaps.mutable.empty();
    public MutableIntObjectMap<Vector2f> offsets = IntObjectMaps.mutable.empty();
    public MutableIntFloatMap restitutions = IntFloatMaps.mutable.empty();

    public Vector2f getSize(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return sizes.get(entityId);
    }
    public void setSize(int entityId, Vector2f size) {
        if (!isPresentOn(entityId)) return;
        sizes.get(entityId).set(size);
    }
    public void setSize(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        sizes.get(entityId).set(x, y);
    }

    public Vector2f getOffset(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return offsets.get(entityId);
    }
    public void setOffset(int entityId, Vector2f offset) {
        if (!isPresentOn(entityId)) return;
        offsets.get(entityId).set(offset);
    }
    public void setOffset(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        offsets.get(entityId).set(x, y);
    }

    public float getRestitution(int entityId) {
        if (!isPresentOn(entityId)) return 0;
        return restitutions.get(entityId);
    }
    public void setRestitution(int entityId, float restitution) {
        if (!isPresentOn(entityId)) return;
        restitutions.put(entityId, restitution);
    }

    @Override
    public void addOnEntity(int entityId) {
        sizes.put(entityId, new Vector2f());
        offsets.put(entityId, new Vector2f());
    }
    @Override
    public void removeFromEntity(int entityId) {
        sizes.remove(entityId);
        offsets.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        if (!isPresentOn(data.entity))
            addOnEntity(data.entity);
        float[] values = SerializationUtils.deserialize(data.value);
        sizes.get(data.entity).set(values[0], values[1]);
        offsets.get(data.entity).set(values[2], values[3]);
        restitutions.put(data.entity, values[4]);
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f size = sizes.get(entityId);
        Vector2f offset = offsets.get(entityId);
        float restitution = restitutions.get(entityId);
        return SerializationUtils.serialize(size.x, size.y, offset.x, offset.y, restitution);
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return sizes.contains(entityId);
    }
}
