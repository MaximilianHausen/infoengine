package net.totodev.infoengine.ecs;

import net.totodev.infoengine.resources.scene.ComponentDataModel;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.jetbrains.annotations.*;

/**
 * A common component that contains just a boolean. Must be subclassed for usage.
 */
public abstract class FlagComponent implements Component {
    private final MutableIntSet set = IntSets.mutable.empty();

    public final boolean isFlaged(int entityId) {
        return set.contains(entityId);
    }

    public final void setFlag(int entityId, boolean value) {
        if (value) set.add(entityId);
        else set.remove(entityId);
    }

    public final void invertFlag(int entityId) {
        if (isFlaged(entityId)) set.remove(entityId);
        else set.add(entityId);
    }

    @Override
    public final void resetEntity(int entityId) {
        setFlag(entityId, false);
    }

    @Override
    public final void deserializeState(@NotNull ComponentDataModel data) {
        setFlag(data.entity, true);
    }
    @Override
    public final @Nullable String serializeState(int entityId) {
        return isFlaged(entityId) ? "" : null;
    }

    @Override
    public final boolean isPresentOn(int entityId) {
        return isFlaged(entityId);
    }
}
