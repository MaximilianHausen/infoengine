package net.totodev.infoengine.util;

import net.totodev.infoengine.util.lambda.Action1;
import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.NotNull;

/**
 * You can add actions to this event and call them with {@link #run(Object)}.<br>
 * An event is itself an action, so it can be used to pass multiple methods as a single action. <br>
 * To unsubscribe from an event, the exact same object that was subscribed has to be used,
 * not just a reference to the same method.
 * @see Action1
 */
public class Event1<T> implements Action1<T> {
    private final MutableList<Action1<T>> subscribers = Lists.mutable.empty();
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
     * @param action The action to add to the event
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
     * Invokes the event and calls all currently registered actions using the specified parameter.
     * Sends a log message if logging is enabled.
     * @param param The parameter to use for calling the actions
     */
    public void run(T param) {
        if (log)
            Logger.log(LogLevel.Debug, "Event", "Invoked " + name);
        for (Action1<T> action : subscribers)
            action.run(param);
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
