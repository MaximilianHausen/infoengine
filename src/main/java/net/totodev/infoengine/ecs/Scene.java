package net.totodev.infoengine.ecs;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Scene {
    public final EventManager events = new EventManager();

    private final MutableIntSet entities = IntSets.mutable.empty();
    private final IntArrayFIFOQueue freeIds = new IntArrayFIFOQueue();
    private int highestId = 0;

    private final MutableMap<Class<? extends IComponent>, IComponent> components = Maps.mutable.empty();
    private final MutableMap<Class<? extends ISystem>, ISystem> systems = Maps.mutable.empty();

    public Scene() {
        CoreEvents.registerAll(events);
    }

    public int createEntity() {
        int newId = freeIds.isEmpty() ? highestId++ : freeIds.dequeueInt();
        events.invokeEvent(CoreEvents.CreateEntity.toString(), newId);
        entities.add(newId);
        events.invokeEvent(CoreEvents.EntityCreated.toString(), newId);
        return newId;
    }

    public void destroyEntity(int entityId) {
        components.forEach((c) -> c.removeFromEntity(entityId));

        events.invokeEvent(CoreEvents.DestroyEntity.toString(), entityId);
        entities.remove(entityId);
        freeIds.enqueue(entityId);
        events.invokeEvent(CoreEvents.EntityDestroyed.toString(), entityId);
    }

    public void addComponent(@NotNull IComponent component) {
        Class<? extends IComponent> componentType = component.getClass();
        components.put(componentType, component);
        events.invokeEvent(CoreEvents.ComponentAdded.toString(), component);
    }

    public void removeComponent(@NotNull Class<? extends IComponent> componentType) {
        IComponent component = components.remove(componentType);
        events.invokeEvent(CoreEvents.ComponentRemoved.toString(), component);
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> T getComponent(@NotNull Class<T> componentType) {
        IComponent temp = components.get(componentType);
        if (temp == null)
            throw new IllegalArgumentException("The component of type " + componentType.getName() + " has not been registered on this EntityManager.");
        return (T) temp;
    }

    public void addSystem(@NotNull ISystem system) {
        systems.put(system.getClass(), system);
        system.initialize(this);
        events.invokeEvent(CoreEvents.SystemAdded.toString(), system);
    }

    public void removeSystem(@NotNull Class<? extends IComponent> systemType) {
        ISystem system = systems.remove(systemType);
        system.deinitialize(this);
        events.invokeEvent(CoreEvents.SystemRemoved.toString(), system);
    }

    public ImmutableIntSet getAllEntities() {
        return entities.toImmutable();
    }

    public @NotNull MutableIntList getEntitiesByComponents(@NotNull Class<? extends IComponent>... componentTypes) {
        ImmutableList<IComponent> requiredComponents = Lists.immutable.fromStream(Arrays.stream(componentTypes).map(this::getComponent));
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
