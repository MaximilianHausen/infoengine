package org.totogames.infoframework.util;

import org.totogames.infoframework.util.logging.LogSeverity;
import org.totogames.infoframework.util.logging.Logger;

import java.util.LinkedList;
import java.util.List;

public class Event {
    private final List<Action0> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public Event(String name, boolean log, Logger logger) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(Action0 action) {
        subscribers.add(action);
    }
    public void unsubscribe(Action0 action) {
        subscribers.remove(action);
    }

    public void invoke() {
        if (log)
            Logger.log(LogSeverity.Debug, "Event", "Invoked " + name);
        for (Action0 action : subscribers)
            action.run();
    }

    public String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
