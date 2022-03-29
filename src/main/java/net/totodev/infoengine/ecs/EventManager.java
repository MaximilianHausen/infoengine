package net.totodev.infoengine.ecs;

import net.totodev.infoengine.util.Action;
import net.totodev.infoengine.util.Action1;
import net.totodev.infoengine.util.Event;
import net.totodev.infoengine.util.Event1;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.tuple.Tuples;

public class EventManager {
    private final MutableIntObjectMap<Event> simpleEvents = IntObjectMaps.mutable.empty();
    private final MutableIntObjectMap<Pair<Event1, Class<?>>> parameterEvents = IntObjectMaps.mutable.empty();
    private final MutableObjectIntMap<String> eventNameToId = ObjectIntMaps.mutable.empty();
    private int highestId = 0;

    //region Simple
    public void registerEvent(String name, boolean logging) {
        if (!eventNameToId.containsKey(name))
            eventNameToId.put(name, highestId++);
        simpleEvents.put(eventNameToId.get(name), new Event(name, logging));
    }

    public void invokeEvent(String name) {
        invokeEvent(eventNameToId.get(name));
    }
    public void invokeEvent(int id) {
        simpleEvents.get(id).run();
    }

    public void subscribe(String name, Action action) {
        subscribe(eventNameToId.get(name), action);
    }
    public void subscribe(int id, Action action) {
        Event event = simpleEvents.get(id);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + id + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        event.subscribe(action);
    }

    public void unsubscribe(String name, Action action) {
        unsubscribe(eventNameToId.get(name), action);
    }
    public void unsubscribe(int id, Action action) {
        Event event = simpleEvents.get(id);
        if (event == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while unsubscribing from event " + id + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        // No type checking because unregistering a wrong type won't break anything (Removing from a list only cares about equality)
        event.unsubscribe(action);
    }
    //endregion
    //region Parameterized
    public void registerEvent(String name, Class<?> parameterType, boolean logging) {
        if (!eventNameToId.containsKey(name))
            eventNameToId.put(name, highestId++);
        parameterEvents.put(eventNameToId.get(name), Tuples.pair(new Event1<>(name, logging), parameterType));
    }

    public void invokeEvent(String name, Object param) {
        invokeEvent(eventNameToId.get(name), param);
    }
    @SuppressWarnings("unchecked")
    public void invokeEvent(int id, Object param) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(id);
        if (param.getClass().isAssignableFrom(eventPair.getTwo())) {
            Logger.log(LogLevel.Error, "EventManager", "Error while invoking event " + id + ": Provided parameter of type " + param.getClass().getName() + " is not assignable to type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().run(param);
    }

    public <T> void subscribe(String name, Class<T> paramType, Action1<T> action) {
        subscribe(eventNameToId.get(name), paramType, action);
    }
    @SuppressWarnings("unchecked")
    public <T> void subscribe(int id, Class<T> paramType, Action1<T> action) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(id);
        if (eventPair == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + id + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        if (paramType.isAssignableFrom(eventPair.getTwo())) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + id + ": Provided parameter of type " + paramType.getName() + " is not assignable to required type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().subscribe(action);
    }

    public void unsubscribe(String name, Action1<?> action) {
        unsubscribe(eventNameToId.get(name), action);
    }
    @SuppressWarnings("unchecked")
    public void unsubscribe(int id, Action1<?> action) {
        Pair<Event1, Class<?>> eventPair = parameterEvents.get(id);
        if (eventPair == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while unsubscribing from event " + id + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        // No type checking because unregistering a wrong type won't break anything (Removing from a list only cares about equality)
        eventPair.getOne().unsubscribe(action);
    }
    //endregion
}
