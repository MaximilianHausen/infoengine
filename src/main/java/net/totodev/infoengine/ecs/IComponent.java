package net.totodev.infoengine.ecs;

import net.totodev.infoengine.loading.ComponentDataModel;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IComponent {
    /**
     * Adds this component to an entity.
     * @param entityId The entity id to add this component to
     */
    void addOnEntity(int entityId);

    /**
     * Removes this component from an entity.
     * @param entityId The entity id to remove this component to
     */
    void removeFromEntity(int entityId);

    /**
     * Sets the component state for one entity.
     * @param data The entity id with the serialized state
     * @see #deserializeAllState(ComponentDataModel...)
     */
    void deserializeState(@NotNull ComponentDataModel data);

    /**
     * Gets the serialized component state for one entity, or null, if this component is not present on that entity.
     * @param entityId The entity to get the state for
     * @return The serialized state for that entity
     */
    @Nullable String serializeState(int entityId);

    /**
     * Sets the component state for multiple entities.
     * @param data List of entity ids with serialized state
     * @see #deserializeState(ComponentDataModel)
     */
    default void deserializeAllState(@NotNull ComponentDataModel... data) {
        for (ComponentDataModel d : data)
            deserializeState(d);
    }

    /**
     * Gets the serialized component state for multiple entities.
     * @param entities Collection of all entities to get the state for
     * @return Array of all the serialized state
     */
    default @NotNull ComponentDataModel[] serializeAllState(@NotNull IntIterable entities) {
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
