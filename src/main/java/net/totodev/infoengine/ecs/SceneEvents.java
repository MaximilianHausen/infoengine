package net.totodev.infoengine.ecs;

import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;

import java.lang.invoke.*;

public class SceneEvents {
    private final MutableMap<String, MutableSet<MethodHandle>> events = Maps.mutable.empty();

    /**
     * @param name The name of the event to invoke
     * @param args The arguments to pass to the event.
     */
    public void invokeEvent(String name, Object... args) {
        MutableSet<MethodHandle> subscribers = events.get(name);
        if (subscribers != null) {
            if (args.length == 0) {
                for (MethodHandle method : subscribers) {
                    try {
                        method.invoke();
                    } catch (WrongMethodTypeException e) {
                        Logger.log(LogLevel.Error, "SceneEvents", "Wrong arguments supplied to event " + name + " [" + e + "]");
                    } catch (Throwable e) {
                        Logger.log(LogLevel.Error, "SceneEvents", "Error while invoking event " + name + " [" + e + "]");
                    }
                }
            } else {
                for (MethodHandle method : subscribers) {
                    try {
                        method.invokeWithArguments(args);
                    } catch (WrongMethodTypeException e) {
                        Logger.log(LogLevel.Error, "SceneEvents", "Wrong arguments supplied to event " + name + " [" + e + "]");
                    } catch (Throwable e) {
                        Logger.log(LogLevel.Error, "SceneEvents", "Error while invoking event " + name + " [" + e + "]");
                    }
                }
            }
        }
    }

    /**
     * Subscribes a method to an event. When subscribing a non-static method,
     * the instance to call it on has to be bound to the MethodHandle in advance with {@link MethodHandle#bindTo(Object)}.
     * @param name   The name of the event to subscribe to
     * @param method The method to subscribe
     */
    public void subscribe(String name, MethodHandle method) {
        MutableSet<MethodHandle> subscribers = events.get(name);

        if (subscribers == null) {
            subscribers = Sets.mutable.empty();
            events.put(name, subscribers);
        }

        subscribers.add(method);
    }

    /**
     * @param name   The name of the event to unsubscribe from
     * @param method The method to unsubscribe
     */
    public void unsubscribe(String name, MethodHandle method) {
        MutableSet<MethodHandle> subscribers = events.get(name);
        if (subscribers == null) return;

        subscribers.remove(method);

        if (subscribers.size() == 0) events.remove(name);
    }
}
