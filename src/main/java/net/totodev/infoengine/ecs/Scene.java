package net.totodev.infoengine.ecs;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.NotNull;

public class Scene {
    //TODO: Custom multi dimensional array
    private final IntSet entities = IntSets.emptySet();
    private final IntArrayFIFOQueue freeIds = new IntArrayFIFOQueue();
    private int highestId = 0;

    private final MutableMap<Class<?>, IComponent> components = Maps.mutable.empty();

    public int createEntity() {
        if (freeIds.isEmpty()) {
            highestId++;
            entities.add(highestId);
            return highestId;
        } else {
            int newId = freeIds.dequeueInt();
            entities.add(newId);
            return newId;
        }
    }

    public void destroyEntity(int entityId) {
        entities.remove(entityId);
        freeIds.enqueue(entityId);
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> @NotNull T getComponent(Class<T> componentClass) {
        IComponent temp = components.get(componentClass);
        if (temp == null)
            throw new IllegalArgumentException("The component of type " + componentClass.getName() + " has not been registered on this EntityManager.");
        return (T) temp;
    }

    public boolean isAlive(int entityId) {
        return entities.contains(entityId);
    }
}
