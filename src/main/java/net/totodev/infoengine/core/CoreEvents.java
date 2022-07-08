package net.totodev.infoengine.core;

/**
 * All default events used by the engine
 */
public final class CoreEvents {
    /**
     * Called during the creation of an entity, before it is added to the scene
     */
    public static final String CreateEntity = "CreateEntity";
    /**
     * Called after an entity was added to the scene
     */
    public static final String EntityCreated = "EntityCreated";

    /**
     * Called during the destruction of an entity, before it is removed from the scene
     */
    public static final String DestroyEntity = "DestroyEntity";
    /**
     * Called after an entity was removed from the scene
     */
    public static final String EntityDestroyed = "EntityDestroyed";

    /**
     * Called after a component was added to the scene
     */
    public static final String ComponentAdded = "ComponentAdded";
    /**
     * Called after a component was removed from the scene
     */
    public static final String ComponentRemoved = "ComponentRemoved";

    /**
     * Called after a global component was added to the scene
     */
    public static final String GlobalComponentAdded = "GlobalComponentAdded";
    /**
     * Called after a global component was removed from the scene
     */
    public static final String GlobalComponentRemoved = "GlobalComponentRemoved";

    /**
     * Called after a system was added to the scene
     */
    public static final String SystemAdded = "SystemAdded";
    /**
     * Called after a component was removed from the scene
     */
    public static final String SystemRemoved = "SystemRemoved";

    /**
     * The main game loop <br/>
     * Parameters: <br/>
     * <pre><code>
     * float deltaTime: The time since the last update in seconds
     * </code></pre>
     */
    public static final String Update = "Update";
}
