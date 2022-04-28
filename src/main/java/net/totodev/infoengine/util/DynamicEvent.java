package net.totodev.infoengine.util;

import net.totodev.infoengine.util.logging.*;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.*;

public class DynamicEvent {
    private final List<MethodHandle> subscribers = new LinkedList<>();
    private String name;
    private boolean log;

    /**
     * Constructs a new event with the name "UnnamedDynamicEvent" and logging disabled.
     */
    public DynamicEvent() {
        this("UnnamedDynamicEvent", false);
    }

    /**
     * @param name The name used when logging
     * @param log  Enables logging when the event is invoked
     */
    public DynamicEvent(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    /**
     * Adds a method handle to the collector.
     * @param method The method handle to remove from the collector
     */
    public void subscribe(@NotNull MethodHandle method) {
        subscribers.add(method);
    }

    /**
     * Removes a method handle from the collector.
     * @param method The method handle to remove from the collector
     */
    public void unsubscribe(@NotNull MethodHandle method) {
        subscribers.remove(method);
    }

    /**
     * Invokes the collector and calls all currently registered method handles using the specified parameters.
     * Sends a log message if logging is enabled.
     * @param args The parameters to use for calling the method handles
     */
    public ArrayList<Object> run(Object... args) {
        if (log)
            Logger.log(LogLevel.Debug, "Event", "Invoked " + name);

        ArrayList<Object> results = new ArrayList<>(subscribers.size());
        for (MethodHandle method : subscribers) {
            try {
                results.add(method.invoke(args));
            } catch (Throwable e) {
                Logger.log(LogLevel.Debug, "Event", "Error while invoking dynamic collector " + name + " [" + e + "]");
            }
        }
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
