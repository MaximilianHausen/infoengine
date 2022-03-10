package net.totodev.infoengine.util;

import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * You can add actions to this event and call them with {@link #run(Object)}.<br>
 * An event is itself an action, so it can be used to pass multiple methods as a single action.
 * @see Action1
 */
public class Event1<T> implements Action1<T> {
    private final List<Action1<T>> subscribers = new LinkedList<>();
    private String name;
    private boolean log;

    /**
     * Constructs a new event with the name "UnnamedEvent" and logging disabled.
     */
    public Event1() {
        this("UnnamedEvent", false);
    }

    /**
     * @param name The name used when logging
     * @param log  Enables logging when the event is invoked
     */
    public Event1(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    /**
     * Adds an action to the event.
     * @param action The action to remove from the event
     */
    public void subscribe(@NotNull Action1<T> action) {
        subscribers.add(action);
    }

    /**
     * Removes an action from the event.
     * @param action The action to remove from the event
     */
    public void unsubscribe(@NotNull Action1<T> action) {
        subscribers.remove(action);
    }

    /**
     * Invokes the event and calls all currently registered actions using the specified arguments.
     * Sends a log message if logging is enabled.
     * @param args The arguments for
     */
    public void run(T args) {
        if (log)
            Logger.log(LogSeverity.Debug, "Event", "Invoked " + name);
        for (Action1<T> action : subscribers)
            action.run(args);
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
