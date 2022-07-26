package net.totodev.engine.resources.scene;

import java.util.Objects;

public class ComponentDataModel {
    public int entity;
    public String value;

    public ComponentDataModel(int entityId, String value) {
        this.entity = entityId;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentDataModel that = (ComponentDataModel) o;
        return entity == that.entity && Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() {
        return Objects.hash(entity, value);
    }
}
