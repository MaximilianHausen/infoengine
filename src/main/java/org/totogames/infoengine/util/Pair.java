package org.totogames.infoengine.util;

import org.jetbrains.annotations.NotNull;

// https://stackoverflow.com/a/521235

/**
 * Stores a immutable pair of values.
 * @param <L> The type of the left value
 * @param <R> The type of the right value
 */
public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(@NotNull L left, @NotNull R right) {
        this.left = left;
        this.right = right;
    }

    public @NotNull L getLeft() {
        return left;
    }
    public @NotNull R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair other)) return false;
        return left.equals(other.getLeft()) && right.equals(other.getRight());
    }
}
