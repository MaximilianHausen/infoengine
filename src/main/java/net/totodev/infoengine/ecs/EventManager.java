package net.totodev.infoengine.ecs;

import net.totodev.infoengine.util.Action;
import net.totodev.infoengine.util.Action1;
import net.totodev.infoengine.util.Event;
import net.totodev.infoengine.util.Event1;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.Tuples;

/**
 * A collection of Events. Before subscribing or invoking an event, you have to register it with a name and its parameter type.
 * There are separate methods for simple and parameterized events.
 */
@SuppressWarnings("rawtypes")
public class EventManager {
    private final MutableMap<String, Event> simpleEvents = Maps.mutable.empty();
    private final MutableMap<String, Pair<Event1, Class<?>>> parameterEvents = Maps.mutable.empty();

    //region Simple
    /**
     * Registers a new simple event.
     * @param name    The name of the new event
     * @param logging Enables logging at each invocation
     */
    public void registerEvent(String name, boolean logging) {
        simpleEvents.put(name, new Event(name, logging));
    }

    /**
     * Invokes a simple event.
     * @param name The name of the event to invoke
     */
    public void invokeEvent(String name) {
        Event event = simpleEvents.get(name);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while invoking simple event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        event.run();
    }

    /**
     * Subscribes a method to a simple event.
     * @param name   The name of the event to subscribe to
     * @param action The method to subscribe
     */
    public void subscribe(String name, Action action) {
        Event event = simpleEvents.get(name);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to simple event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        event.subscribe(action);
    }

    /**
     * Unsubscribes a method from a simple event. Be careful when unsubscribing anonymous methods/lambdas.
     * @param name   The name of the event to unsubscribe from
     * @param action The method to unsubscribe
     */
    public void unsubscribe(String name, Action action) {
        Event event = simpleEvents.get(name);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while unsubscribing from simple event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        event.unsubscribe(action);
    }
    //endregion
    //region Parameterized
    /**
     * Registers a new parameterized event.
     * @param name          The name of the new event
     * @param parameterType The type of the event parameter
     * @param logging       Enables logging at each invocation
     */
    public void registerEvent(String name, Class<?> parameterType, boolean logging) {
        parameterEvents.put(name, Tuples.pair(new Event1<>(name, logging), parameterType));
    }

    /**
     * Invokes a parameterized event.
     * @param name The name of the event to invoke
     * @param arg  The argument to pass to the event
     */
    @SuppressWarnings("unchecked")
    public void invokeEvent(String name, Object arg) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(name);
        if (!eventPair.getTwo().isInstance(arg)) {
            Logger.log(LogLevel.Error, "EventManager", "Error while invoking parameterized event " + name + ": Provided parameter of type " + arg.getClass().getName() + " is not assignable from required type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().run(arg);
    }

    /**
     * Subscribes a method to a parameterized event.
     * @param name      The name of the event to subscribe to
     * @param paramType Only needed because of type erasure
     * @param action    The method to subscribe
     * @param <T>       The type of the parameter of the method
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(String name, Class<T> paramType, Action1<T> action) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(name);
        if (eventPair == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to parameterized event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        if (!paramType.isAssignableFrom(eventPair.getTwo())) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to parameterized event " + name + ": Provided parameter of type " + paramType.getName() + " is not assignable from required type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().subscribe(action);
    }

    /**
     * Unsubscribes a method from a parameterized event. Be careful when unsubscribing anonymous methods/lambdas.
     * @param name   The name of the event to unsubscribe from
     * @param action The method to unsubscribe
     */
    @SuppressWarnings("unchecked")
    public void unsubscribe(String name, Action1<?> action) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(name);
        if (eventPair == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while unsubscribing from parameterized event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        // No type checking because unregistering a wrong type won't break anything (Removing from a list only cares about equality)
        eventPair.getOne().unsubscribe(action);
    }
    //endregion
}
