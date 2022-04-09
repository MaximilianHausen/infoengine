package net.totodev.infoengine.loading;

import java.util.Objects;

public class ComponentDataModel {
    public int entity;
    public String data;

    public ComponentDataModel(int entityId, String data) {
        this.entity = entityId;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentDataModel that = (ComponentDataModel) o;
        return entity == that.entity && Objects.equals(data, that.data);
    }
    @Override
    public int hashCode() {
        return Objects.hash(entity, data);
    }
}
