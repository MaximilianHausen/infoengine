package net.totodev.infoengine.util;

import net.totodev.infoengine.util.logging.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Like an event, but with a return type.
 * You can add funcs to this collector and call them with {@link #run()}.
 * @see Event
 * @see Func
 */
public class Collector<TResult> implements Func<ArrayList<TResult>> {
    private final List<Func<TResult>> subscribers = new LinkedList<>();
    private String name;
    private boolean log;

    /**
     * Constructs a new collector with the name "UnnamedCollector" and logging disabled.
     */
    public Collector() {
        this("UnnamedCollector", false);
    }

    /**
     * @param name The name used when logging
     * @param log  Enables logging when the event is invoked
     */
    public Collector(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    /**
     * Adds a func to the collector.
     * @param func The func to add to the collector
     */
    public void subscribe(@NotNull Func<TResult> func) {
        subscribers.add(func);
    }

    /**
     * Removes a func from the collector.
     * @param func The func to remove from the collector
     */
    public void unsubscribe(@NotNull Func<TResult> func) {
        subscribers.remove(func);
    }

    /**
     * Invokes the collector and calls all currently registered funcs. Sends a log message if logging is enabled.
     * @return A list of all return values
     */
    public ArrayList<TResult> run() {
        if (log)
            Logger.log(LogLevel.Debug, "Collector", "Invoked " + name);

        ArrayList<TResult> results = new ArrayList<>(subscribers.size());
        for (Func<TResult> func : subscribers)
            results.add(func.run());
        return results;
    }

    public @NotNull String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogging() {
        return log;
    }
    public void setLogging(boolean log) {
        this.log = log;
    }
}
