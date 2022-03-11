package net.totodev.infoengine.tests.util;

import net.totodev.infoengine.tests.CamelCaseGenerator;
import net.totodev.infoengine.util.Action;
import net.totodev.infoengine.util.Event;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class EventTests {
    private int counter = 0;

    @Test
    public void logs() {
        Event event = new Event("TestEvent", true);
        Logger.setLogLevel(LogLevel.Debug);
        Logger.setLogTarget(s -> counter++);
        event.run();
        Assertions.assertEquals(2, counter);
    }

    @Test
    public void subscribeAndRun() {
        Event event = new Event();
        event.subscribe(() -> counter++);
        event.run();
        Assertions.assertEquals(1, counter);
    }

    @Test
    public void unsubscribe() {
        Event event = new Event();
        Action action = () -> counter++;
        event.subscribe(action);
        event.unsubscribe(action);
        event.run();
        Assertions.assertEquals(0, counter);
    }
}
