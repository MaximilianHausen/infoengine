package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import net.totodev.infoengine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;

public class Camera2d implements Component {
    private final MutableIntObjectMap<Vector2f> sizes = IntObjectMaps.mutable.empty();
    private final MutableIntObjectMap<Vector2f> offsets = IntObjectMaps.mutable.empty();

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

    @Override
    public void resetEntity(int entityId) {
        sizes.remove(entityId);
        offsets.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        float[] values = SerializationUtils.deserialize(data.value);
        setSize(data.entity, values[0], values[1]);
        setOffset(data.entity, values[2], values[3]);
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f size = sizes.get(entityId);
        Vector2f offset = offsets.get(entityId);
        return SerializationUtils.serialize(size.x, size.y, offset.x, offset.y);
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return sizes.containsKey(entityId) && offsets.containsKey(entityId);
    }
}
