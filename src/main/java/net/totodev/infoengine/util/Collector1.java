package net.totodev.infoengine.util;

import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Like an event, but with a return type.
 * You can add funcs to this collector and call them with {@link #run(Object)}.
 * @see Event
 * @see Func
 */
public class Collector1<TResult, TParam> implements Func1<ArrayList<TResult>, TParam> {
    private final List<Func1<TResult, TParam>> subscribers = new LinkedList<>();
    private String name;
    private boolean log;

    /**
     * Constructs a new collector with the name "UnnamedCollector" and logging disabled.
     */
    public Collector1() {
        this("UnnamedCollector", false);
    }

    /**
     * @param name The name used when logging
     * @param log  Enables logging when the event is invoked
     */
    public Collector1(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    /**
     * Adds a func to the collector.
     * @param func The func to add to the collector
     */
    public void subscribe(@NotNull Func1<TResult, TParam> func) {
        subscribers.add(func);
    }

    /**
     * Removes a func from the collector.
     * @param func The func to remove from the collector
     */
    public void unsubscribe(@NotNull Func1<TResult, TParam> func) {
        subscribers.remove(func);
    }

    /**
     * Invokes the collector and calls all currently registered funcs. Sends a log message if logging is enabled.
     * @param param The parameter use for calling the funcs
     * @return A list of all return values
     */
    public ArrayList<TResult> run(TParam param) {
        if (log)
            Logger.log(LogSeverity.Debug, "Collector", "Invoked " + name);

        ArrayList<TResult> results = new ArrayList<>(subscribers.size());
        for (Func1<TResult, TParam> func : subscribers)
            results.add(func.run(param));
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
