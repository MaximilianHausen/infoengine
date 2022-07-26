package net.totodev.engine.ecs;

import java.lang.annotation.*;

/**
 * ONLY USE THIS ON IComponent OR IGlobalComponent <br/>
 * This annotation can be put on fields in systems to automatically fill them with the fitting component from the scene.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedComponent {
}
