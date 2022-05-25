package net.totodev.infoengine.core;

import net.totodev.infoengine.ecs.EventManager;

/**
 * All default events used by the engine
 */
public enum CoreEvents {
    /**
     * Called during the creation of an entity, before it is added to the scene
     */
    CreateEntity("CreateEntity", false),
    /**
     * Called after an entity was added to the scene
     */
    EntityCreated("EntityCreated", false),

    /**
     * Called during the destruction of an entity, before it is removed from the scene
     */
    DestroyEntity("DestroyEntity", false),
    /**
     * Called after an entity was removed from the scene
     */
    EntityDestroyed("EntityDestroyed", false),

    /**
     * Called after a component was added to the scene
     */
    ComponentAdded("ComponentAdded", false),
    /**
     * Called after a component was removed from the scene
     */
    ComponentRemoved("ComponentRemoved", false),

    /**
     * Called after a global component was added to the scene
     */
    GlobalComponentAdded("GlobalComponentAdded", false),
    /**
     * Called after a global component was removed from the scene
     */
    GlobalComponentRemoved("GlobalComponentRemoved", false),

    /**
     * Called after a system was added to the scene
     */
    SystemAdded("SystemAdded", false),
    /**
     * Called after a component was removed from the scene
     */
    SystemRemoved("SystemRemoved", false),

    Update("Update", false);

    private final String name;
    private final boolean logging;

    CoreEvents(String name, boolean logging) {
        this.name = name;
        this.logging = logging;
    }

    /**
     * Registers all core events.
     * @param eventManager The event manager to register the events on
     */
    public static void registerAll(EventManager eventManager) {
        for (CoreEvents event : CoreEvents.values())
            eventManager.registerEvent(event.name, event.logging);
    }

    public String getName() {
        return name;
    }
}
