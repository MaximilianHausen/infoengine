package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.opengl.enums.VertexAttribDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;

/**
 * All information needed to set a vertex attribute
 */
public record VertexAttribute(@NotNull Buffer vertexBuffer, @NotNull VertexAttribDataType type,
                              @Range(from = 1, to = 4) int size) {
    private static final HashMap<VertexAttribDataType, Integer> dataSizes;

    static {
        // Initialize data sizes
        HashMap<VertexAttribDataType, Integer> temp = new HashMap<>();
        temp.put(VertexAttribDataType.BYTE, 1);
        temp.put(VertexAttribDataType.UNSIGNED_BYTE, 1);
        temp.put(VertexAttribDataType.SHORT, 2);
        temp.put(VertexAttribDataType.UNSIGNED_SHORT, 2);
        temp.put(VertexAttribDataType.INT, 4);
        temp.put(VertexAttribDataType.UNSIGNED_INT, 4);
        temp.put(VertexAttribDataType.HALF_FLOAT, 2);
        temp.put(VertexAttribDataType.FLOAT, 4);
        temp.put(VertexAttribDataType.DOUBLE, 8);
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
