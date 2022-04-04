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

//TODO: Component/System lifetime events
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
        events.invokeEvent(CoreEvents.DestroyEntity.toString(), entityId);
        entities.remove(entityId);
        freeIds.enqueue(entityId);
        events.invokeEvent(CoreEvents.EntityDestroyed.toString(), entityId);
    }

    public void registerComponent(@NotNull IComponent component) {
        components.put(component.getClass(), component);
    }

    public void unregisterComponent(@NotNull Class<? extends IComponent> componentType) {
        components.remove(componentType);
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

        //TODO: Better system init
        system.initialize(this);
    }

    public void removeSystem(@NotNull Class<? extends IComponent> componentType) {
        systems.remove(componentType);
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
