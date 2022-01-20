package org.totogames.infoengine.util;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.LinkedList;
import java.util.List;

public class Event1<T> implements Action1<T> {
    private final List<Action1<T>> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public Event1() {
        this("UnnamedArgEvent", false);
    }

    public Event1(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(@NotNull Action1<T> action) {
        subscribers.add(action);
    }
    public void unsubscribe(@NotNull Action1<T> action) {
        subscribers.remove(action);
    }

    public void run(T args) {
        if (log)
            Logger.log(LogSeverity.Debug, "Event", "Invoked " + name);
        for (Action1<T> action : subscribers)
            action.run(args);
    }

    public @NotNull String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
