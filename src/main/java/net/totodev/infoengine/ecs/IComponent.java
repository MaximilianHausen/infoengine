package net.totodev.infoengine.ecs;

import net.totodev.infoengine.loading.ComponentDataModel;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.jetbrains.annotations.Nullable;

public interface IComponent {
    void deserializeState(ComponentDataModel data);

    default void deserializeAllState(ComponentDataModel... data) {
        for (ComponentDataModel d : data)
            deserializeState(d);
    }

    @Nullable String serializeState(int entityId);

    default ComponentDataModel[] serializeAllState(IntIterable entities) {
        MutableList<ComponentDataModel> temp = Lists.mutable.empty();
        entities.forEach(entityId -> {
            String state = serializeState(entityId);
            if (state != null)
                temp.add(new ComponentDataModel(entityId, state));
        });
        return (ComponentDataModel[]) temp.toArray();
    }

    boolean isPresentOn(int entityId);
}
