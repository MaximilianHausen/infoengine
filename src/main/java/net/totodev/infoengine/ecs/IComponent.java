package net.totodev.infoengine.ecs;

import net.totodev.infoengine.loading.ComponentDataModel;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.jetbrains.annotations.Nullable;

public interface IComponent {
    Scene getScene();

    void deserializeState(ComponentDataModel... data);

    @Nullable String serializeState(int entityId);

    default ComponentDataModel[] serializeAllState() {
        MutableList<ComponentDataModel> temp = Lists.mutable.empty();
        getScene().getAllEntities().forEach(entityId -> {
            String state = serializeState(entityId);
            if (state != null)
                temp.add(new ComponentDataModel(entityId, state));
        });
        return (ComponentDataModel[]) temp.toArray();
    }

    boolean isPresentOn(int entityId);
}
