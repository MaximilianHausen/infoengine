package org.totodev.engine.resources.scene;

import java.util.*;

public class SceneModel {
    public String formatVersion;
    public String name;

    public int entityCount;

    public String[] systems = new String[0];
    public ComponentModel[] components = new ComponentModel[0];
    public GlobalComponentModel[] globalComponents = new GlobalComponentModel[0];

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SceneModel that = (SceneModel) o;
        return entityCount == that.entityCount && Objects.equals(formatVersion, that.formatVersion) && Objects.equals(name, that.name) && Arrays.equals(systems, that.systems) && Arrays.equals(components, that.components) && Arrays.equals(globalComponents, that.globalComponents);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(formatVersion, name, entityCount);
        result = 31 * result + Arrays.hashCode(systems);
        result = 31 * result + Arrays.hashCode(components);
        result = 31 * result + Arrays.hashCode(globalComponents);
        return result;
    }
}
