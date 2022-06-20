package net.totodev.infoengine.resources;

import net.totodev.infoengine.resources.image.ImageResource;
import org.eclipse.collections.api.factory.*;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;

import java.io.File;
import java.nio.file.*;

public class ResourceManager {
    private static final MutableMap<String, ImageResource> images = Maps.mutable.empty();

    public static void loadResourcePack(File directory) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException("File " + directory + " is not a directory");
        Path dirPath = directory.toPath();
        loadImagesRecursive(Paths.get(directory.getPath(), "textures").toFile())
                .forEach(i -> images.put(dirPath.relativize(i.getFile().toPath()).toString(), i));
    }

    private static MutableList<ImageResource> loadImagesRecursive(File directory) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException("File " + directory + " is not a directory");

        MutableList<ImageResource> temp = Lists.mutable.empty();
        MutableList<File> nextSteps = Lists.mutable.empty();

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                nextSteps.add(file);
                continue;
            }

            if (file.getPath().endsWith(".png"))
                temp.add(new ImageResource(file));
        }

        nextSteps.forEach(d -> temp.addAllIterable(loadImagesRecursive(d)));

        return temp;
    }
}
