package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.totogames.infoengine.rendering.opengl.enums.VertexAttribDataType;

import java.util.HashMap;

import static org.totogames.infoengine.rendering.opengl.enums.VertexAttribDataType.*;

/**
 * All information needed to set a vertex attribute
 */
public record VertexAttribute(@NotNull Buffer vertexBuffer, @NotNull  VertexAttribDataType type, @Range(from = 1, to = 4) int size) {
    private static final HashMap<VertexAttribDataType, Integer> dataSizes;

    static {
        // Initialize data sizes
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

    /**
     * Gets the total size of this vertex array in bytes.
     * @return The size in bytes
     */
    public @Range(from = 0, to = Integer.MAX_VALUE) int getByteSize() {
        return dataSizes.get(type) * size;
    }
}
