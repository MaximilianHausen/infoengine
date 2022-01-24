package org.totogames.infoengine.rendering.opengl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This method requires the object to be bound before use
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface RequiresBind {
}
