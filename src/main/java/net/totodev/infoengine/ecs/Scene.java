package net.totodev.infoengine.ecs;

import net.totodev.infoengine.core.CoreEvents;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.primitive.*;
import org.eclipse.collections.api.stack.primitive.MutableIntStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Scene {
    public final EventManager events = new EventManager();

    private final MutableIntSet entities = IntSets.mutable.empty();
    private final MutableIntStack freeIds = IntStacks.mutable.empty(); //TODO: FIFO Queue
    private int highestId = 0;

    private final MutableMap<Class<? extends IComponent>, IComponent> components = Maps.mutable.empty();
    private final MutableMap<Class<? extends IGlobalComponent>, IGlobalComponent> globalComponents = Maps.mutable.empty();
    private final MutableMap<Class<? extends ISystem>, ISystem> systems = Maps.mutable.empty();

    private boolean isRunning = false;

    public Scene() {
        CoreEvents.registerAll(events);
    }

    public void start() {
        if (isRunning) return;
        for (ISystem system : systems)
            system.start(this);
        isRunning = true;
    }

    public void stop() {
        if (!isRunning) return;
        for (ISystem system : systems)
            system.stop(this);
        isRunning = false;
    }

    /**
     * Creates a new entity.
     * @return The id of the new entity
     */
    public int createEntity() {
        int newId = freeIds.isEmpty() ? highestId++ : freeIds.pop();
        events.invokeEvent(CoreEvents.CreateEntity.getName(), newId);
        entities.add(newId);
        events.invokeEvent(CoreEvents.EntityCreated.getName(), newId);
        return newId;
    }

    /**
     * Destroys an entity and removes it from all components.
     * @param entityId The id of the entity to destroy
     */
    public void destroyEntity(int entityId) {
        if (!isAlive(entityId)) return;

        components.forEach((c) -> c.removeFromEntity(entityId));
        events.invokeEvent(CoreEvents.DestroyEntity.getName(), entityId);

        entities.remove(entityId);
        freeIds.push(entityId);
        events.invokeEvent(CoreEvents.EntityDestroyed.getName(), entityId);
    }

    /**
     * Adds a component to this scene. If a component of this type has already been added, it will be overwritten.
     * @param component The component to add
     */
    public void addComponent(@NotNull IComponent component) {
        Class<? extends IComponent> componentType = component.getClass();
        components.put(componentType, component);
        events.invokeEvent(CoreEvents.ComponentAdded.getName(), component);
    }

    /**
     * Removes a component from this scene.
     * @param componentType The runtime type of the component to remove
     */
    public void removeComponent(@NotNull Class<? extends IComponent> componentType) {
        IComponent component = components.remove(componentType);
        events.invokeEvent(CoreEvents.ComponentRemoved.getName(), component);
    }

    /**
     * Retrieves a component from this scene
     * @param componentType The runtime type of the component to retrieve
     * @param <T>           The type of the component to retrieve. Only used to downcast the retrieved component.
     * @return The retrieved component
     */
    @SuppressWarnings("unchecked")
    public <T extends IComponent> T getComponent(@NotNull Class<T> componentType) {
        IComponent temp = components.get(componentType);
        if (temp == null)
            Logger.log(LogLevel.Error, "Scene", "Unable to find component of type " + componentType.getName() + " in this scene.");
        return (T) temp;
    }

    /**
     * Adds a global component to this scene. If a global component of this type has already been added, it will be overwritten.
     * @param component The global component to add
     */
    public void addGlobalComponent(@NotNull IGlobalComponent component) {
        Class<? extends IGlobalComponent> componentType = component.getClass();
        globalComponents.put(componentType, component);
        events.invokeEvent(CoreEvents.GlobalComponentAdded.getName(), component);
    }

    /**
     * Removes a global component from this scene.
     * @param componentType The runtime type of the global component to remove
     */
    public void removeGlobalComponent(@NotNull Class<? extends IGlobalComponent> componentType) {
        IGlobalComponent component = globalComponents.remove(componentType);
        events.invokeEvent(CoreEvents.GlobalComponentRemoved.getName(), component);
    }

    /**
     * Retrieves a global component from this scene
     * @param componentType The runtime type of the global component to retrieve
     * @param <T>           The type of the global component to retrieve. Only used to downcast the retrieved component.
     * @return The retrieved component
     */
    @SuppressWarnings("unchecked")
    public <T extends IGlobalComponent> T getGlobalComponent(@NotNull Class<T> componentType) {
        IGlobalComponent temp = globalComponents.get(componentType);
        if (temp == null)
            Logger.log(LogLevel.Error, "Scene", "Unable to find global component of type " + componentType.getName() + " in this scene.");
        return (T) temp;
    }

    /**
     * Adds a system to this scene. If a system of this type has already been added, it will be overwritten.
     * @param system The system to add
     */
    public void addSystem(@NotNull ISystem system) {
        systems.put(system.getClass(), system);
        system.added(this);
        if (isRunning) system.start(this);
        events.invokeEvent(CoreEvents.SystemAdded.getName(), system);
    }

    /**
     * Removes a system from this scene.
     * @param systemType The runtime type of the system to remove
     */
    public void removeSystem(@NotNull Class<? extends ISystem> systemType) {
        ISystem system = systems.remove(systemType);
        system.removed(this);
        events.invokeEvent(CoreEvents.SystemRemoved.getName(), system);
    }

    public ImmutableIntSet getAllEntities() {
        return entities.toImmutable();
    }

    /**
     * Gets a list of all entities that have every specified component. Components not added to this scene will be ignored.
     * @param componentTypes The components to check for
     * @return A list of all entities that have all the specified components
     */
    public @NotNull IntList getEntitiesByComponents(@NotNull Class<? extends IComponent>... componentTypes) {
        // Get and filter instances for component types
        ImmutableList<IComponent> requiredComponents = Lists.immutable
                .fromStream(Arrays.stream(componentTypes).map(this::getComponent).filter(Objects::nonNull));

        MutableIntList temp = IntLists.mutable.empty();
        for (int e : entities.toArray()) {
            boolean hasAllComponents = true;
            for (IComponent c : requiredComponents)
                if (!c.isPresentOn(e)) hasAllComponents = false;
            if (hasAllComponents) temp.add(e);
        }

        return temp;
    }

    /**
     * Checks if an entity with this id currently exists in this scene
     * @param entityId The entity id to check for
     * @return Whether an entity with this id exists in this scene
     */
    public boolean isAlive(int entityId) {
        return entities.contains(entityId);
    }
}
