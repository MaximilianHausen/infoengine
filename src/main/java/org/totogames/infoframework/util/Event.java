package org.totogames.infoframework.util;

import org.totogames.infoframework.util.logging.LogSeverity;
import org.totogames.infoframework.util.logging.Logger;

import java.util.LinkedList;
import java.util.List;

public class Event implements Action {
    private final List<Action> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public Event() {
        this("UnnamedEvent", false);
    }

    public Event(String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(Action action) {
        subscribers.add(action);
    }
    public void unsubscribe(Action action) {
        subscribers.remove(action);
    }

    public void run() {
        if (log)
            Logger.log(LogSeverity.Debug, "Event", "Invoked " + name);
        for (Action action : subscribers)
            action.run();
    }

    public String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
