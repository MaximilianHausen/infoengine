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

    public void registerEvent(String name, Class<?> parameterType) {
        if (!eventNameToId.containsKey(name))
            eventNameToId.put(name, highestId++);
        events.put(eventNameToId.getInt(name), Tuples.pair(new Event1<>(name, false), parameterType));
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

    public <T> void subscribe(String name, Action1<T> action, Class<T> paramType) {
        subscribe(eventNameToId.getInt(name), action, paramType);
    }

    @SuppressWarnings("unchecked")
    public <T> void subscribe(int id, Action1<T> action, Class<T> paramType) {
        Pair<Event1, Class<?>> eventPair = events.get(id);
        if (paramType.isAssignableFrom(eventPair.getTwo())) {
            Logger.log(LogLevel.Error, "EventManager", "Error while subscribing to event " + id + ": Provided parameter of type " + paramType.getName() + " is not assignable to type " + eventPair.getTwo().getName() + ".");
            return;
        }
        eventPair.getOne().subscribe(action);
    }
}
