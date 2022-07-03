package net.totodev.infoengine.util;

import java.nio.ByteBuffer;

/**
 * An object that can be written to a ByteBuffer.
 * If you implement this, you should also have a static BYTES constant that is equivalent to the bytes() method.
 */
public interface BufferWritable {
    int bytes();
    void writeToBuffer(ByteBuffer buffer, int offset);
}
