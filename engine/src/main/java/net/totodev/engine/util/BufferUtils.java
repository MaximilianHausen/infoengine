package net.totodev.engine.util;

import org.eclipse.collections.api.RichIterable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

public class BufferUtils {
    public static PointerBuffer asPointerBuffer(RichIterable<String> values) {
        MemoryStack stack = MemoryStack.stackGet();

        PointerBuffer buffer = stack.mallocPointer(values.size());
        values.forEach((x) -> buffer.put(stack.UTF8(x)));

        return buffer.rewind();
    }
}
