package net.totodev.infoengine.util;

import java.nio.ByteBuffer;

public interface BufferWritable {
    int bytes();
    void writeToBuffer(ByteBuffer buffer);
}
