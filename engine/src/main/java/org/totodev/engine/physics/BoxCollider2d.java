package org.totodev.engine.physics;

import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;
import org.totodev.engine.ecs.Component;
import org.totodev.engine.resources.scene.ComponentDataModel;
import org.totodev.engine.util.SerializationUtils;

import java.util.Arrays;

public class BoxCollider2d implements Component {
    public MutableIntObjectMap<Vector2f> sizes = IntObjectMaps.mutable.empty();
    public MutableIntObjectMap<Vector2f> offsets = IntObjectMaps.mutable.empty();
    public MutableIntFloatMap restitutions = IntFloatMaps.mutable.empty();
    public MutableIntIntMap layers = IntIntMaps.mutable.empty();
    public MutableIntIntMap types = IntIntMaps.mutable.empty();

    //region Size
    public Vector2f getSize(int entityId, @NotNull Vector2f out) {
        Vector2f size = sizes.get(entityId);
        if (size == null) return null;
        return out.set(size);
    }

    public void setSize(int entityId, @NotNull Vector2f size) {
        sizes.put(entityId, size);
    }
    public void setSize(int entityId, float x, float y) {
        sizes.put(entityId, new Vector2f(x, y));
    }
    //endregion

    //region Offset
    public Vector2f getOffset(int entityId, @NotNull Vector2f out) {
        Vector2f offset = offsets.get(entityId);
        if (offset == null) return null;
        return out.set(offset);
    }

    public void setOffset(int entityId, @NotNull Vector2f offset) {
        offsets.put(entityId, offset);
    }
    public void setOffset(int entityId, float x, float y) {
        offsets.put(entityId, new Vector2f(x, y));
    }
    //endregion

    //region Restitution
    public float getRestitution(int entityId) {
        return restitutions.get(entityId);
    }

    public void setRestitution(int entityId, float restitution) {
        restitutions.put(entityId, restitution);
    }
    //endregion

    //region Layer
    public int getLayer(int entityId) {
        return layers.get(entityId);
    }

    public void setLayer(int entityId, int layer) {
        layers.put(entityId, layer);
    }
    //endregion

    //region Type
    public int getType(int entityId) {
        return types.get(entityId);
    }

    public void setType(int entityId, int type) {
        types.put(entityId, type);
    }
    //endregion

    @Override
    public void resetEntity(int entityId) {
        sizes.remove(entityId);
        offsets.remove(entityId);
        restitutions.remove(entityId);
        layers.remove(entityId);
        types.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        String[] splitValue = data.value.split("\\|");

        String[] floatStrings = Arrays.copyOfRange(splitValue, 0, 5);
        float[] floats = SerializationUtils.deserialize(floatStrings);
        setSize(data.entity, floats[0], floats[1]);
        setOffset(data.entity, floats[2], floats[3]);
        setRestitution(data.entity, floats[4]);

        setLayer(data.entity, Integer.parseInt(splitValue[5]));
        setType(data.entity, Integer.parseInt(splitValue[6]));
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f size = sizes.get(entityId);
        Vector2f offset = offsets.get(entityId);
        float restitution = restitutions.get(entityId);
        return SerializationUtils.serialize(size.x, size.y, offset.x, offset.y, restitution) + '|' + layers.get(entityId) + '|' + types.get(entityId);
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return sizes.containsKey(entityId)
                && offsets.containsKey(entityId)
                && restitutions.containsKey(entityId)
                && layers.containsKey(entityId)
                && types.containsKey(entityId);
    }
}
