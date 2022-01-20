package org.totogames.infoengine.util;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class IO {
    public static @NotNull String getTextFromFile(@NotNull File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            Logger.log(LogSeverity.Error, "IO", "Text could not be read from file <" + file.toPath() + ">");
            return "";
        }
    }

    public static File getFileFromResource(@NotNull String fileName) {
        ClassLoader classLoader = IO.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        try {
            if (resource == null) return null;
            else return new File(resource.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
