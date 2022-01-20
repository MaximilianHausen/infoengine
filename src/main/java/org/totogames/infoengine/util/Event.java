package org.totogames.infoengine.util;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.LinkedList;
import java.util.List;

public class Event implements Action {
    private final List<Action> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public Event() {
        this("UnnamedEvent", false);
    }

    public Event(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(@NotNull Action action) {
        subscribers.add(action);
    }
    public void unsubscribe(@NotNull Action action) {
        subscribers.remove(action);
    }

    public void run() {
        if (log)
            Logger.log(LogSeverity.Debug, "Event", "Invoked " + name);
        for (Action action : subscribers)
            action.run();
    }

    public @NotNull String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
