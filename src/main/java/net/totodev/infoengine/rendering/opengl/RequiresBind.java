package net.totodev.infoengine.rendering.opengl;

import java.lang.annotation.*;

/**
 * This method requires the object to be bound before use
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface RequiresBind {
}
