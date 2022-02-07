package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.totogames.infoengine.rendering.opengl.enums.VertexAttribDataType;

import java.util.HashMap;

import static org.totogames.infoengine.rendering.opengl.enums.VertexAttribDataType.*;

public class VertexAttribute {
    private static final HashMap<VertexAttribDataType, Integer> dataSizes;

    static {
        HashMap<VertexAttribDataType, Integer> temp = new HashMap<>();
        temp.put(BYTE, 1);
        temp.put(UNSIGNED_BYTE, 1);
        temp.put(SHORT, 2);
        temp.put(UNSIGNED_SHORT, 2);
        temp.put(INT, 4);
        temp.put(UNSIGNED_INT, 4);
        temp.put(HALF_FLOAT, 2);
        temp.put(FLOAT, 4);
        temp.put(DOUBLE, 8);
        //TODO: All data types
        //temp.put(UNSIGNED_INT_2_10_10_10_REV);
        //temp.put(INT_2_10_10_10_REV);
        //temp.put(FIXED);
        dataSizes = temp;
    }

    private final Buffer vertexBuffer;
    private final int size;
    private final VertexAttribDataType type;

    public VertexAttribute(@NotNull Buffer vertexBuffer, VertexAttribDataType type, @Range(from = 1, to = 4) int size) {
        this.vertexBuffer = vertexBuffer;
        this.type = type;
        this.size = size;
    }

    public Buffer getVertexBuffer() {
        return vertexBuffer;
    }
    public VertexAttribDataType getType() {
        return type;
    }
    public int getSize() {
        return size;
    }

    public @Range(from = 0, to = Integer.MAX_VALUE) int getByteSize() {
        return dataSizes.get(type) * size;
    }
}
