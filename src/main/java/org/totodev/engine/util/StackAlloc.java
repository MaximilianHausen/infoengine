package org.totodev.engine.util;

import java.lang.annotation.*;

/**
 * The item this method returns contains values that are allocated on the parent stack and don't have to be freed manually.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface StackAlloc {
}
