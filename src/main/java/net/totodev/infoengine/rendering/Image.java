package net.totodev.infoengine.rendering;

import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

/**
 * A byte buffer with associated image data.
 */
public final class Image implements AutoCloseable {
    private final ByteBuffer pixels;
    private final int width;
    private final int height;
    private final int channels;

    private boolean closed = false;

    public Image(ByteBuffer pixels, int width, int height, int channels) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    public ByteBuffer getPixels() {
        if (closed) throw new ImageClosedException();
        return pixels;
    }

    public int getWidth() {
        if (closed) throw new ImageClosedException();
        return width;
    }

    public int getHeight() {
        if (closed) throw new ImageClosedException();
        return height;
    }

    public int getChannels() {
        if (closed) throw new ImageClosedException();
        return channels;
    }

    @Override
    public void close() {
        if (closed) return;
        STBImage.stbi_image_free(pixels);
        closed = true;
    }

    /**
     * This Exception is thrown when calling a method on a previously closed image.
     */
    public static class ImageClosedException extends RuntimeException {
        public ImageClosedException() {
            super("Image was already closed");
        }
    }
}
