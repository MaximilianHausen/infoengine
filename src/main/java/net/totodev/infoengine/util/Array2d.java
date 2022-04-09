package net.totodev.infoengine.util;

import java.util.Objects;

/**
 * A two dimensional array implemented as a one dimensional array with offsets
 * @param <T> The type of the elements
 */
public class Array2d<T> {
    public final int lenX;
    public final int lenY;
    public final Object[] values;

    public Array2d(int x, int y) {
        values = new Object[x * y];
        lenX = x;
        lenY = y;
    }

    public void set(int x, int y, T element) {
        int index = y * lenY + x;
        Objects.checkIndex(index, values.length);
        values[index] = element;
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        int index = y * lenY + x;
        Objects.checkIndex(index, values.length);
        return (T) values[index];
    }
}
