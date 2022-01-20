package org.totogames.infoengine.ecs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Scene {
    private final List<Entity> entities = new ArrayList<>(); // TODO: Linked vs Array

    public @UnmodifiableView @NotNull List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void add(@NotNull Entity entity) {
        entities.add(entity);
        entity.added(this);
    }
    public void remove(@NotNull Entity entity) {
        entity.setParent(null);
        entities.remove(entity);
        entity.removed(this);
    }

    public void beforeUpdate() {
        for (Entity entity : entities) {
            entity.beforeUpdate();
        }
    }
    public void update() {
        for (Entity entity : entities) {
            entity.update();
        }
    }
    public void afterUpdate() {
        for (Entity entity : entities) {
            entity.afterUpdate();
        }
    }

    public void beforeRender() {
        for (Entity entity : entities) {
            entity.beforeRender();
        }
    }
    public void render() {
        for (Entity entity : entities) {
            entity.render();
        }
    }
    public void afterRender() {
        for (Entity entity : entities) {
            entity.afterRender();
        }
    }
}
