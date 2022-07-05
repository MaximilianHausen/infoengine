package net.totodev.infoengine.physics;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import net.totodev.infoengine.util.SerializationUtils;
import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;

import java.util.Arrays;

public class BoxCollider2d implements Component {
    public MutableIntObjectMap<Vector2f> sizes = IntObjectMaps.mutable.empty();
    public MutableIntObjectMap<Vector2f> offsets = IntObjectMaps.mutable.empty();
    public MutableIntFloatMap restitutions = IntFloatMaps.mutable.empty();
    public MutableIntObjectMap<String> collisionTypes = IntObjectMaps.mutable.empty();

    //region Size
    public Vector2f getSize(int entityId) {
        return sizes.get(entityId);
    }
    public void setSize(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        sizes.get(entityId).set(x, y);
    }
    public void setSize(int entityId, Vector2f size) {
        setSize(entityId, size.x, size.y);
    }
    //endregion

    //region Offset
    public Vector2f getOffset(int entityId) {
        return offsets.get(entityId);
    }
    public void setOffset(int entityId, float x, float y) {
        if (!isPresentOn(entityId)) return;
        offsets.get(entityId).set(x, y);
    }
    public void setOffset(int entityId, Vector2f offset) {
        setOffset(entityId, offset.x, offset.y);
    }
    //endregion

    //region Restitution
    public float getRestitution(int entityId) {
        return restitutions.get(entityId);
    }
    public void setRestitution(int entityId, float restitution) {
        if (!isPresentOn(entityId)) return;
        restitutions.put(entityId, restitution);
    }
    //endregion

    //region CollisionType
    public String getCollisionType(int entityId) {
        return collisionTypes.get(entityId);
    }
    public void setCollisionType(int entityId, String collisionType) {
        if (!isPresentOn(entityId)) return;
        collisionTypes.put(entityId, collisionType);
    }
    //endregion

    @Override
    public void addOnEntity(int entityId) {
        sizes.put(entityId, new Vector2f());
        offsets.put(entityId, new Vector2f());
        restitutions.remove(entityId); // Still gets 0 as default
        collisionTypes.put(entityId, "");
    }
    @Override
    public void removeFromEntity(int entityId) {
        sizes.remove(entityId);
        offsets.remove(entityId);
        restitutions.remove(entityId);
        collisionTypes.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        if (!isPresentOn(data.entity))
            addOnEntity(data.entity);

        String[] splitValue = data.value.split("\\|");
        String[] numberStrings = Arrays.copyOfRange(splitValue, 0, 5);

        float[] numbers = SerializationUtils.deserialize(numberStrings);
        sizes.get(data.entity).set(numbers[0], numbers[1]);
        offsets.get(data.entity).set(numbers[2], numbers[3]);
        restitutions.put(data.entity, numbers[4]);
        collisionTypes.put(data.entity, String.join("|", Arrays.copyOfRange(splitValue, 5, splitValue.length)));
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        Vector2f size = sizes.get(entityId);
        Vector2f offset = offsets.get(entityId);
        float restitution = restitutions.get(entityId);
        return SerializationUtils.serialize(size.x, size.y, offset.x, offset.y, restitution) + '|' + collisionTypes.get(entityId);
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return sizes.contains(entityId);
    }
}
