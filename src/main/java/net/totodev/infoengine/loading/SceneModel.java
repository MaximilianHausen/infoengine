package net.totodev.infoengine.loading;

import java.util.Arrays;
import java.util.Objects;

public class SceneModel {
    public String formatVersion;
    public String name;

    public int entityCount;

    public String[] systems = new String[0];
    public ComponentModel[] components = new ComponentModel[0];

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SceneModel that = (SceneModel) o;
        return entityCount == that.entityCount && formatVersion.equals(that.formatVersion) && name.equals(that.name) && Arrays.equals(systems, that.systems) && Arrays.equals(components, that.components);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(formatVersion, name, entityCount);
        result = 31 * result + Arrays.hashCode(systems);
        result = 31 * result + Arrays.hashCode(components);
        return result;
    }
}
