package net.totodev.infoengine.util;

import net.totodev.infoengine.util.logging.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * You can add actions to this event and call them with {@link #run()}.<br>
 * An event is itself an action, so it can be used to pass multiple methods as a single action.
 * @see Action
 */
public class Event implements Action {
    private final List<Action> subscribers = new LinkedList<>();
    private String name;
    private boolean log;

    /**
     * Constructs a new event with the name "UnnamedEvent" and logging disabled.
     */
    public Event() {
        this("UnnamedEvent", false);
    }

    /**
     * @param name The name used when logging
     * @param log  Enables logging when the event is invoked
     */
    public Event(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    /**
     * Adds an action to the event.
     * @param action The action to add to the event
     */
    public void subscribe(@NotNull Action action) {
        subscribers.add(action);
    }

    /**
     * Removes an action from the event.
     * @param action The action to remove from the event
     */
    public void unsubscribe(@NotNull Action action) {
        subscribers.remove(action);
    }

    /**
     * Invokes the event and calls all currently registered actions.
     * Sends a log message if logging is enabled.
     */
    public void run() {
        if (log)
            Logger.log(LogLevel.Debug, "Event", "Invoked " + name);
        for (Action action : subscribers)
            action.run();
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
