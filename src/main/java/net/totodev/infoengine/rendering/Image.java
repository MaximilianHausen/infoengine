package net.totodev.infoengine.rendering;

import java.nio.ByteBuffer;

/**
 * A byte buffer with associated image data
 */
public class Image {
    private final ByteBuffer pixels;
    private final int width;
    private final int height;
    private final int channels;

    public Image(ByteBuffer pixels, int width, int height, int channels) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    public ByteBuffer getPixels() {
        return pixels;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getChannels() {
        return channels;
    }
}
