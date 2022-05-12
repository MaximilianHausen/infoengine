package net.totodev.infoengine.loading;

import java.util.Objects;

public class GlobalComponentModel {
    public String type;
    public String value;

    public GlobalComponentModel(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalComponentModel that = (GlobalComponentModel) o;
        return Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
