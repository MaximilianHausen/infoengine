package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.IComponent;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import net.totodev.infoengine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;

public class Camera2d implements IComponent {
    private final MutableIntObjectMap<Vector2f> sizes = IntObjectMaps.mutable.empty();
    private final MutableIntObjectMap<Vector2f> offsets = IntObjectMaps.mutable.empty();

    public Vector2f getSize(int entityId, @NotNull Vector2f out) {
        if (!isPresentOn(entityId)) return null;
        return out.set(sizes.get(entityId));
    }
    public void setSize(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        sizes.get(entityId).set(x, y);
    }
    public void setSize(int entityId, @NotNull Vector2f size) {
        setSize(entityId, size.x, size.y);
    }

    public Vector2f getOffset(int entityId, @NotNull Vector2f out) {
        if (!isPresentOn(entityId)) return null;
        return out.set(offsets.get(entityId));
    }
    public void setOffset(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        offsets.get(entityId).set(x, y);
    }
    public void setOffset(int entityId, @NotNull Vector2f offset) {
        setOffset(entityId, offset.x, offset.y);
    }

    //region IComponent
    public void addOnEntity(int entityId) {
        sizes.put(entityId, new Vector2f());
        offsets.put(entityId, new Vector2f());
    }
    public void removeFromEntity(int entityId) {
        sizes.remove(entityId);
        offsets.remove(entityId);
    }
    public void deserializeState(@NotNull ComponentDataModel data) {
        if (!isPresentOn(data.entity))
            addOnEntity(data.entity);

        float[] deserializedData = SerializationUtils.deserialize(data.value);
        sizes.get(data.entity).set(deserializedData[0], deserializedData[1]);
        offsets.get(data.entity).set(deserializedData[2], deserializedData[3]);
    }
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f size = sizes.get(entityId);
        Vector2f offset = offsets.get(entityId);
        return SerializationUtils.serialize(size.x, size.y, offset.x, offset.y);
    }
    public boolean isPresentOn(int entityId) {
        return sizes.containsKey(entityId);
    }
    //endregion
}
