package net.totodev.infoengine.resources;

import java.io.File;

public interface Resource {
    File getFile();

    /**
     * @return The resource key of this resource, if it was loaded through the {@link ResourceManager}. Otherwise null
     */
    default String getResourceKey() {
        return ResourceManager.getResourceKey(this);
    }

    void load();
    void unload();
}
