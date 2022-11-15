package org.totodev.engine.rendering;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;
import org.joml.Vector2i;
import org.totodev.engine.ecs.Component;
import org.totodev.engine.resources.ResourceManager;
import org.totodev.engine.resources.image.ImageResource;
import org.totodev.engine.resources.scene.ComponentDataModel;

public class Sprite2d implements Component {
    private final MutableIntObjectMap<ImageResource> images = IntObjectMaps.mutable.empty();

    public ImageResource getSprite(int entityId) {
        return images.get(entityId);
    }

    public void setSprite(int entityId, ImageResource image) {
        images.put(entityId, image);
    }

    public Vector2i getSize(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return images.get(entityId).getSize();
    }

    @Override
    public void resetEntity(int entityId) {
        images.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        setSprite(data.entity, ResourceManager.getImage(data.value));
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
}
