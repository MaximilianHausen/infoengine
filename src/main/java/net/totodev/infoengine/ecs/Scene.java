package net.totodev.infoengine.ecs;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Scene {
    public final EventManager events = new EventManager();
    //TODO: Custom multi dimensional array
    private final MutableIntSet entities = IntSets.mutable.empty();
    private final IntArrayFIFOQueue freeIds = new IntArrayFIFOQueue();
    private final MutableMap<Class<?>, IComponent> components = Maps.mutable.empty();
    private int highestId = 0;

    public Scene() {
        CoreEvents.registerAll(events);
    }

    public int createEntity() {
        int newId = freeIds.isEmpty() ? highestId++ : freeIds.dequeueInt();
        entities.add(newId);
        events.invokeEvent("EntityCreated", newId);
        return newId;
    }

    public void destroyEntity(int entityId) {
        entities.remove(entityId);
        freeIds.enqueue(entityId);
        events.invokeEvent("EntityDestroyed", entityId);
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> @NotNull T getComponent(Class<T> componentClass) {
        IComponent temp = components.get(componentClass);
        if (temp == null)
            throw new IllegalArgumentException("The component of type " + componentClass.getName() + " has not been registered on this EntityManager.");
        return (T) temp;
    }

    public @NotNull MutableIntList getEntitiesByComponents(@NotNull Class<IComponent>... componentClasses) {
        ImmutableList<IComponent> requiredComponents = Lists.immutable.fromStream(Arrays.stream(componentClasses).map(this::getComponent));
        MutableIntList temp = IntLists.mutable.empty();
        for (int i : entities.toArray())
            for (IComponent c : requiredComponents)
                if (c.isPresentOn(i))
                    temp.add(i);

        return temp;
    }

    public boolean isAlive(int entityId) {
        return entities.contains(entityId);
    }
}
