package net.totodev.infoengine.ecs;

import net.totodev.infoengine.resources.scene.ComponentDataModel;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.jetbrains.annotations.*;

public class FlagComponent implements Component {
    private final MutableIntSet set = IntSets.mutable.empty();

    @Override
    public void addOnEntity(int entityId) {
        set.add(entityId);
    }
    @Override
    public void removeFromEntity(int entityId) {
        set.remove(entityId);
    }

    @Override
    public void deserializeState(@NotNull ComponentDataModel data) {
        addOnEntity(data.entity);
    }
    @Override
    public @Nullable String serializeState(int entityId) {
        return set.contains(entityId) ? "" : null;
    }

    @Override
    public boolean isPresentOn(int entityId) {
        return set.contains(entityId);
    }
}
