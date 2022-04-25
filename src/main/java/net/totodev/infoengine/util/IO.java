package net.totodev.infoengine.util;

import net.totodev.infoengine.rendering.Image;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;

/**
 * Temporary helper class for IO, used until proper resource handling is implemented
 */
public class IO {
    public static @NotNull String getTextFromFile(@NotNull File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            Logger.log(LogLevel.Error, "IO", "Text could not be read from file <" + file.toPath() + ">");
            return "";
        }
    }

    public static @Nullable File getFileFromResource(@NotNull String fileName) {
        ClassLoader classLoader = IO.class.getClassLoader();
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
        int[] width = new int[1], height = new int[1], channels = new int[1];
        ByteBuffer pixels = STBImage.stbi_load(file.getPath(), width, height, channels, desiredChannels);
        return new Image(pixels, width[0], height[0], desiredChannels);
    }
}
