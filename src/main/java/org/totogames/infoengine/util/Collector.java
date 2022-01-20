package org.totogames.infoengine.util;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

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

    public Collector(@NotNull String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(@NotNull Func<TResult> func) {
        subscribers.add(func);
    }
    public void unsubscribe(@NotNull Func<TResult> func) {
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

    public @NotNull String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
