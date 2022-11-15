package org.totodev.engine.ecs;

import java.lang.annotation.*;

/**
 * This annotation can be put on methods in systems to automatically register them to the specified event on start().
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSubscriber {
    String value();
}
