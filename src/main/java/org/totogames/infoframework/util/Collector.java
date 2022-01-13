package org.totogames.infoframework.util;

import org.totogames.infoframework.util.logging.LogSeverity;
import org.totogames.infoframework.util.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Collector<TResult> implements Func<ArrayList<TResult>> {
    private final List<Func<TResult>> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public Collector() {
        this("UnnamedCollector", false);
    }

    public Collector(String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(Func<TResult> func) {
        subscribers.add(func);
    }
    public void unsubscribe(Func<TResult> func) {
        subscribers.remove(func);
    }

    public ArrayList<TResult> run() {
        if (log)
            Logger.log(LogSeverity.Debug, "Collector", "Invoked " + name);

        ArrayList<TResult> results = new ArrayList<>(subscribers.size());
        for (Func<TResult> func : subscribers)
            results.add(func.run());
        return results;
    }

    public String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
