package net.totodev.infoengine.ecs;

public enum CoreEvents {
    /**
     * Called before an entity is added to the scene
     */
    CreateEntity("CreateEntity", Integer.class, false),
    /**
     * Called after an entity was added to the scene
     */
    EntityCreated("EntityCreated", Integer.class, false),

    /**
     * Called before an entity is removed from the scene
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

    public static void registerAll(EventManager eventManager) {
        for (CoreEvents event : CoreEvents.values()) {
            if (event.parameterType == null)
                eventManager.registerEvent(event.name, event.logging);
            else
                eventManager.registerEvent(event.name, event.parameterType, event.logging);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
