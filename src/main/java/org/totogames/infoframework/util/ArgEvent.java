package org.totogames.infoframework.util;

import org.totogames.infoframework.util.logging.LogSeverity;
import org.totogames.infoframework.util.logging.Logger;

import java.util.LinkedList;
import java.util.List;

public class ArgEvent<T> implements Action1<T> {
    private final List<Action1<T>> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public ArgEvent() {
        this("UnnamedArgEvent", false);
    }

    public ArgEvent(String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(Action1<T> action) {
        subscribers.add(action);
    }
    public void unsubscribe(Action1<T> action) {
        subscribers.remove(action);
    }

    public void run(T args) {
        if (log)
            Logger.log(LogSeverity.Debug, "Event", "Invoked " + name);
        for (Action1<T> action : subscribers)
            action.run(args);
    }

    public String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
