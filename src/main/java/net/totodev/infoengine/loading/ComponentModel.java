package net.totodev.infoengine.loading;

import java.util.Arrays;
import java.util.Objects;

public class ComponentModel {
    public String type;
    public ComponentDataModel[] values;

    public ComponentModel(String type, ComponentDataModel... values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentModel that = (ComponentModel) o;
        return Objects.equals(type, that.type) && Arrays.equals(values, that.values);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }
}
