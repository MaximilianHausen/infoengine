package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.resources.ResourceManager;
import net.totodev.infoengine.resources.image.ImageResource;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;

public class Sprite2d implements Component {
    private final MutableIntObjectMap<ImageResource> images = IntObjectMaps.mutable.empty();

    public ImageResource getSprite(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return images.get(entityId);
    }
    public void setSprite(int entityId, ImageResource image) {
        if (!isPresentOn(entityId)) return;
        images.put(entityId, image);
    }

    public Vector2f getSize(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return images.get(entityId).getSize();
    }

    //region IComponent
    @Override
    public void addOnEntity(int entityId) {
        images.put(entityId, null);
    }
    @Override
    public void removeFromEntity(int entityId) {
        images.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        images.put(data.entity, ResourceManager.getImage(data.value));
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return images.get(entityId).getResourceKey();
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return images.containsKey(entityId);
    }
    //endregion
}
