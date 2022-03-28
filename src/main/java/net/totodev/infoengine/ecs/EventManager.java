package net.totodev.infoengine.ecs;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.totodev.infoengine.util.Action1;
import net.totodev.infoengine.util.Event1;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

public class EventManager {
    private final Int2ObjectMap<Pair<Event1, Class<?>>> events = new Int2ObjectArrayMap<>();
    private final Object2IntMap<String> eventNameToId = new Object2IntArrayMap<>();
    private int highestId = 0;

    public void registerEvent(String name, Class<?> parameterType, boolean logging) {
        if (!eventNameToId.containsKey(name))
            eventNameToId.put(name, highestId++);
        events.put(eventNameToId.getInt(name), Tuples.pair(new Event1<>(name, logging), parameterType));
    }

    public void invokeEvent(String name, Object param) {
        invokeEvent(eventNameToId.getInt(name), param);
    }

    @SuppressWarnings("unchecked")
    public void invokeEvent(int id, Object param) {
        Pair<Event1, Class<?>> eventPair = events.get(id);
        if (param.getClass().isAssignableFrom(eventPair.getTwo())) {
            Logger.log(LogLevel.Error, "EventManager", "Error while invoking event " + id + ": Provided parameter of type " + param.getClass().getName() + " is not assignable to type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().run(param);
    }

    public <T> void subscribe(String name, Class<T> paramType, Action1<T> action) {
        subscribe(eventNameToId.getInt(name), paramType, action);
    }

    @SuppressWarnings("unchecked")
    public <T> void subscribe(int id, Class<T> paramType, Action1<T> action) {
        Pair<Event1, Class<?>> eventPair = events.get(id);
        if (eventPair == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + id + ": This event could not be found. Maybe you forgot to register it?");
        }
        if (paramType.isAssignableFrom(eventPair.getTwo())) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + id + ": Provided parameter of type " + paramType.getName() + " is not assignable to type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().subscribe(action);
    }

    public void unsubscribe(String name, Action1<?> action) {
        unsubscribe(eventNameToId.getInt(name), action);
    }

    @SuppressWarnings("unchecked")
    public void unsubscribe(int id, Action1<?> action) {
        Pair<Event1, Class<?>> eventPair = events.get(id);
        if (eventPair == null) {
            Logger.log(LogLevel.Error, "EventManager", "Error while unsubscribing from event " + id + ": This event could not be found. Maybe you forgot to register it?");
            return;
        }
        // No type checking because unregistering a wrong type won't break anything (Removing from a list only cares about equality)
        eventPair.getOne().unsubscribe(action);
    }
}
