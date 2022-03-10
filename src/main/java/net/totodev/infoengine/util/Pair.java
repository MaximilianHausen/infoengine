package net.totodev.infoengine.util;

import org.jetbrains.annotations.NotNull;

/**
 * Stores a immutable pair of values.
 * @param <L> The type of the left value
 * @param <R> The type of the right value
 */
public record Pair<L, R>(@NotNull L left, @NotNull R right) {
}
