package net.totodev.infoengine.tests.util;

import net.totodev.infoengine.tests.CamelCaseGenerator;
import net.totodev.infoengine.util.Collector;
import net.totodev.infoengine.util.Func;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class CollectorTests {
    private int counter = 0;

    @Test
    public void logs() {
        Collector<String> collector = new Collector<>("TestCollector", true);
        Logger.setLogLevel(LogLevel.Debug);
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
