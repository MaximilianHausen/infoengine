package net.totodev.infoengine.ecs;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSubscriber {
    String value();
}
