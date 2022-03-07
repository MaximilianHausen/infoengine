package org.totogames.infoengine.rendering.opengl.wrappers;

import java.lang.annotation.*;

/**
 * This method requires the object to be bound before use
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface RequiresBind {
}
