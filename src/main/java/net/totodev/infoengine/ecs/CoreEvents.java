package net.totodev.infoengine.ecs;

/**
 * All default events used by the engine
 */
public enum CoreEvents {
    /**
     * Called during the creation of an entity, before it is added to the scene
     */
    CreateEntity("CreateEntity", Integer.class, false),
    /**
     * Called after an entity was added to the scene
     */
    EntityCreated("EntityCreated", Integer.class, false),

    /**
     * Called during the destruction of an entity, before it is removed from the scene
     */
    DestroyEntity("DestroyEntity", Integer.class, false),
    /**
     * Called after an entity was removed from the scene
     */
    EntityDestroyed("EntityDestroyed", Integer.class, false),

    /**
     * Called after a component was added to the scene
     */
    ComponentAdded("ComponentAdded", IComponent.class, false),
    /**
     * Called after a component was removed from the scene
     */
    ComponentRemoved("ComponentRemoved", IComponent.class, false),

    /**
     * Called after a global component was added to the scene
     */
    GlobalComponentAdded("GlobalComponentAdded", IGlobalComponent.class, false),
    /**
     * Called after a global component was removed from the scene
     */
    GlobalComponentRemoved("GlobalComponentRemoved", IGlobalComponent.class, false),

    /**
     * Called after a system was added to the scene
     */
    SystemAdded("SystemAdded", ISystem.class, false),
    /**
     * Called after a component was removed from the scene
     */
    SystemRemoved("SystemRemoved", ISystem.class, false),

    Update("Update", null, false);

    private final String name;
    private final Class<?> parameterType;
    private final boolean logging;

    CoreEvents(String name, Class<?> parameterType, boolean logging) {
        this.name = name;
        this.parameterType = parameterType;
        this.logging = logging;
    }

    /**
     * Registers all core events.
     * @param eventManager The event manager to register the events on
     */
    public static void registerAll(EventManager eventManager) {
        for (CoreEvents event : CoreEvents.values()) {
            if (event.parameterType == null)
                eventManager.registerEvent(event.name, event.logging);
            else
                eventManager.registerEvent(event.name, event.parameterType, event.logging);
        }
    }

    public String getName() {
        return name;
    }
}
