package net.totodev.infoengine.util;

import java.nio.ByteBuffer;

public interface IBufferWritable {
    int bytes();
    void writeToBuffer(ByteBuffer buffer);
}
