package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.IComponent;
import net.totodev.infoengine.resources.ResourceManager;
import net.totodev.infoengine.resources.image.ImageResource;
import net.totodev.infoengine.resources.scene.ComponentDataModel;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.*;

public class Sprite2d implements IComponent {
    private final MutableIntObjectMap<ImageResource> images = IntObjectMaps.mutable.empty();

    public ImageResource getSprite(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return images.get(entityId);
    }
    public void setSprite(int entityId, ImageResource image) {
        if (!isPresentOn(entityId)) return;
        images.put(entityId, image);
    }

    //region IComponent
    public void addOnEntity(int entityId) {
        images.put(entityId, null);
    }
    public void removeFromEntity(int entityId) {
        images.remove(entityId);
    }
    public void deserializeState(@NotNull ComponentDataModel data) {
        images.put(data.entity, ResourceManager.getImage(data.value));
    }
    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;
        return images.get(entityId).getResourceKey();
    }
    public boolean isPresentOn(int entityId) {
        return images.containsKey(entityId);
    }
    //endregion
}
