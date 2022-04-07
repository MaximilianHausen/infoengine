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

@SuppressWarnings("rawtypes")
public class EventManager {
    private final MutableMap<String, Event> simpleEvents = Maps.mutable.empty();
    private final MutableMap<String, Pair<Event1, Class<?>>> parameterEvents = Maps.mutable.empty();

    //region Simple
    public void registerEvent(String name, boolean logging) {
        simpleEvents.put(name, new Event(name, logging));
    }

    public void invokeEvent(String name) {
        simpleEvents.get(name).run();
    }

    public void subscribe(String name, Action action) {
        Event event = simpleEvents.get(name);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to simple event " + name + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        event.subscribe(action);
    }

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
    public void registerEvent(String name, Class<?> parameterType, boolean logging) {
        parameterEvents.put(name, Tuples.pair(new Event1<>(name, logging), parameterType));
    }

    @SuppressWarnings("unchecked")
    public void invokeEvent(String name, Object param) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(name);
        if (!eventPair.getTwo().isInstance(param)) {
            Logger.log(LogLevel.Error, "EventManager", "Error while invoking parameterized event " + name + ": Provided parameter of type " + param.getClass().getName() + " is not assignable from required type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().run(param);
    }

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
