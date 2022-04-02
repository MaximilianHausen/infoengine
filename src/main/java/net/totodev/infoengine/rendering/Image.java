package net.totodev.infoengine.rendering;

import java.nio.ByteBuffer;

/**
 * A byte buffer with associated image data
 */
public record Image(ByteBuffer pixels, int width, int height, int channels) {
}
