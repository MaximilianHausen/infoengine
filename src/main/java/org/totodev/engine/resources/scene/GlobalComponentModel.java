package org.totodev.engine.resources.scene;

import java.util.Objects;

public class GlobalComponentModel {
    public String type;
    public String data;

    public GlobalComponentModel(String type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalComponentModel that = (GlobalComponentModel) o;
        return Objects.equals(type, that.type) && Objects.equals(data, that.data);
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, data);
    }
}
