package net.totodev.infoengine.util;

import net.totodev.infoengine.core.Engine;
import net.totodev.infoengine.rendering.Image;
import net.totodev.infoengine.util.logging.*;
import org.jetbrains.annotations.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.file.Files;

import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Temporary helper class for IO, used until proper resource handling is implemented
 */
public class IO {
    public static ByteBuffer readFromResource(String filePath, boolean fromEngine) {
        ClassLoader classLoader = fromEngine ? Engine.class.getClassLoader() : ClassLoader.getSystemClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            return readAllBytes(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static ByteBuffer readAllBytes(InputStream inputStream) {
        try {
            MemoryStack stack = MemoryStack.stackGet();
            return stack.bytes(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull String getTextFromFile(@NotNull File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            Logger.log(LogLevel.Error, "IO", "Text could not be read from file <" + file.toPath() + ">");
            return "";
        }
    }

    public static File getFileFromResource(@NotNull String fileName, boolean fromEngine) {
        ClassLoader classLoader = fromEngine ? Engine.class.getClassLoader() : ClassLoader.getSystemClassLoader();
        URL resource = classLoader.getResource(fileName);
        try {
            if (resource == null) return null;
            else return new File(resource.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static @NotNull Image loadImageFromFile(@NotNull File file) {
        return loadImageFromFile(file, 4);
    }

    public static @NotNull Image loadImageFromFile(@NotNull File file, @Range(from = 1, to = 4) int desiredChannels) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.callocInt(1), height = stack.callocInt(1), channels = stack.callocInt(1);
            ByteBuffer pixels = STBImage.stbi_load(file.getPath(), width, height, channels, desiredChannels);
            return new Image(pixels, width.get(0), height.get(0), desiredChannels);
        }
    }
}
