package org.totogames.infoengine.util;

import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Collector1<TResult, TParam> implements Func1<ArrayList<TResult>, TParam> {
    private final List<Func1<TResult, TParam>> subscribers = new LinkedList<>();
    private final String name;
    private final boolean log;

    public Collector1() {
        this("UnnamedCollector", false);
    }

    public Collector1(String name, boolean log) {
        this.log = log;
        this.name = name;
    }

    public void subscribe(Func1<TResult, TParam> func) {
        subscribers.add(func);
    }
    public void unsubscribe(Func1<TResult, TParam> func) {
        subscribers.remove(func);
    }

    public ArrayList<TResult> run(TParam param) {
        if (log)
            Logger.log(LogSeverity.Debug, "Collector", "Invoked " + name);

        ArrayList<TResult> results = new ArrayList<>(subscribers.size());
        for (Func1<TResult, TParam> func : subscribers)
            results.add(func.run(param));
        return results;
    }

    public String getName() {
        return name;
    }
    public boolean isLogging() {
        return log;
    }
}
