package net.totodev.infoengine.util;

import net.totodev.infoengine.util.logging.*;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.*;

/**
 * An event implementation based on MethodHandles.
 * No safety guarantees are provided, so it is recommended not to use these directly
 * and instead access events through the annotation based API of {@link net.totodev.infoengine.ecs.EventManager} where applicable.
 */
public class HandleEvent {
    private final MutableSet<MethodHandle> subscribers = Sets.mutable.empty();
    public String name;
    public boolean log;

    /**
     * Constructs a new collector with the name "UnnamedHandleEvent" and logging disabled.
     */
    public HandleEvent() {
        this("UnnamedHandleEvent", false);
    }

    /**
     * @param name The name used when logging
     * @param log  Enables logging when the event is invoked
     */
    public HandleEvent(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    /**
     * Adds a method handle to the event.
     * @param method The method handle to remove from the event
     */
    public void subscribe(@NotNull MethodHandle method) {
        subscribers.add(method);
    }

    /**
     * Removes a method handle from the event.
     * @param method The method handle to remove from the event
     */
    public void unsubscribe(@NotNull MethodHandle method) {
        subscribers.remove(method);
    }

    /**
     * Invokes this event and calls all currently registered method handles using the specified parameters.
     * Sends a log message if logging is enabled.
     * @param args The arguments to use for calling the method handles
     */
    public void run(Object... args) {
        if (log)
            Logger.log(LogLevel.Debug, "Event", "Invoked " + name);

        if (args.length == 0) {
            for (MethodHandle method : subscribers) {
                try {
                    method.invoke();
                } catch (WrongMethodTypeException e) {
                    Logger.log(LogLevel.Error, "Event", "Wrong arguments supplied to handle event " + name + " [" + e + "]");
                } catch (Throwable e) {
                    Logger.log(LogLevel.Error, "Event", "Error while invoking handle event " + name + " [" + e + "]");
                }
            }
        } else {
            for (MethodHandle method : subscribers) {
                try {
                    method.invokeWithArguments(args);
                } catch (WrongMethodTypeException e) {
                    Logger.log(LogLevel.Error, "Event", "Wrong arguments supplied to handle event " + name + " [" + e + "]");
                } catch (Throwable e) {
                    Logger.log(LogLevel.Error, "Event", "Error while invoking handle event " + name + " [" + e + "]");
                }
            }
        }
    }
}
