package net.totodev.infoengine.ecs;

import net.totodev.infoengine.util.HandleEvent;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import java.lang.invoke.MethodHandle;

/**
 * A collection of HandleEvents. Events have to be registered before being able to subscribe to or invoke them.
 */
public class EventManager {
    private final MutableMap<String, HandleEvent> events = Maps.mutable.empty();

    /**
     * @param name    The name of the new event
     * @param logging Enables logging at each invocation
     */
    public void registerEvent(String name, boolean logging) {
        events.put(name, new HandleEvent(name, logging));
    }

    /**
     * @param name The name of the event to invoke
     * @param args The arguments to pass to the event.
     */
    public void invokeEvent(String name, Object... args) {
        events.get(name).run(args);
    }

    /**
     * Subscribes a method to an event. When subscribing a non-static method,
     * the instance to call it on has to be bound to the MethodHandle in advance with {@link MethodHandle#bindTo(Object)}.
     * @param name   The name of the event to subscribe to
     * @param method The method to subscribe
     */
    public void subscribe(String name, MethodHandle method) {
        HandleEvent event = events.get(name);

        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }

        event.subscribe(method);
    }

    /**
     * @param name   The name of the event to unsubscribe from
     * @param method The method to unsubscribe
     */
    public void unsubscribe(String name, MethodHandle method) {
        HandleEvent event = events.get(name);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while unsubscribing from event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        event.unsubscribe(method);
    }
}
