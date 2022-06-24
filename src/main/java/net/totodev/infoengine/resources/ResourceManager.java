package net.totodev.infoengine.resources;

import net.totodev.infoengine.resources.image.ImageResource;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.factory.*;
import org.eclipse.collections.api.list.MutableList;

import java.io.File;
import java.nio.file.*;

public class ResourceManager {
    private static final MutableBiMap<String, ImageResource> images = BiMaps.mutable.empty();

    public static String getResourceKey(IResource resource) {
        //Throw if not loaded
        return images.inverse().get(resource);
    }

    public static ImageResource getImage(String resourceKey) {
        return images.get(resourceKey);
    }

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
