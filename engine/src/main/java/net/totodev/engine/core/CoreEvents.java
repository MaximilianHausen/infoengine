package net.totodev.engine.core;

/**
 * All default events used by the engine
 */
public final class CoreEvents {
    /**
     * Called during the creation of an entity, before it is added to the scene
     */
    public static final String CREATE_ENTITY = "CreateEntity";
    /**
     * Called after an entity was added to the scene
     */
    public static final String ENTITY_CREATED = "EntityCreated";

    /**
     * Called during the destruction of an entity, before it is removed from the scene
     */
    public static final String DESTROY_ENTITY = "DestroyEntity";
    /**
     * Called after an entity was removed from the scene
     */
    public static final String ENTITY_DESTROYED = "EntityDestroyed";

    /**
     * Called after a component was added to the scene
     */
    public static final String COMPONENT_ADDED = "ComponentAdded";
    /**
     * Called after a component was removed from the scene
     */
    public static final String COMPONENT_REMOVED = "ComponentRemoved";

    /**
     * Called after a global component was added to the scene
     */
    public static final String GLOBAL_COMPONENT_ADDED = "GlobalComponentAdded";
    /**
     * Called after a global component was removed from the scene
     */
    public static final String GLOBAL_COMPONENT_REMOVED = "GlobalComponentRemoved";

    /**
     * Called after a system was added to the scene
     */
    public static final String SYSTEM_ADDED = "SystemAdded";
    /**
     * Called after a component was removed from the scene
     */
    public static final String SYSTEM_REMOVED = "SystemRemoved";
}
