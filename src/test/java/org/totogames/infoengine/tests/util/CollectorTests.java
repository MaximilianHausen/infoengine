package org.totogames.infoengine.tests.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.totogames.infoengine.tests.CamelCaseGenerator;
import org.totogames.infoengine.util.Collector;
import org.totogames.infoengine.util.Func;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.List;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class CollectorTests {
    private int counter = 0;

    @Test
    public void logs() {
        Collector<String> collector = new Collector<>("TestCollector", true);
        Logger.setLogLevel(LogSeverity.Debug);
        Logger.setLogTarget(s -> counter++);
        collector.run();
        Assertions.assertEquals(2, counter);
    }

    @Test
    public void subscribeAndRun() {
        Collector<String> collector = new Collector<>();
        collector.subscribe(() -> "Test");
        List<String> results = collector.run();
        Assertions.assertEquals("Test", results.get(0));
    }

    @Test
    public void unsubscribe() {
        Collector<String> collector = new Collector<>();
        Func<String> func = () -> "Test";
        collector.subscribe(func);
        collector.unsubscribe(func);
        List<String> results = collector.run();
        Assertions.assertEquals(0, results.size());
    }
}
