package net.totodev.engine.resources.scene;

import java.util.*;

public class ComponentModel {
    public String type;
    public ComponentDataModel[] data;

    public ComponentModel(String type, ComponentDataModel... data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentModel that = (ComponentModel) o;
        return Objects.equals(type, that.type) && Arrays.equals(data, that.data);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
