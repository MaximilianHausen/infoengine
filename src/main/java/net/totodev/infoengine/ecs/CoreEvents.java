package net.totodev.infoengine.ecs;

public enum CoreEvents {
    EntityCreated("EntityCreated", Integer.class, false),
    EntityDestroyed("EntityDestroyed", Integer.class, false);

    public final String name;
    public final Class<?> parameterType;
    public final boolean logging;

    CoreEvents(String name, Class<?> parameterType, boolean logging) {
        this.name = name;
        this.parameterType = parameterType;
        this.logging = logging;
    }

    public static void registerAll(EventManager eventManager) {
        for (CoreEvents event : CoreEvents.values()) {
            eventManager.registerEvent(event.name, event.parameterType, event.logging);
        }
    }
}
