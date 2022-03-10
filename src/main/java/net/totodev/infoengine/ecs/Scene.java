package net.totodev.infoengine.ecs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Scene {
    private final List<Entity> entities = new ArrayList<>();

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

    void addSilent(@NotNull Entity entity) {
        entities.add(entity);
    }
    void removeSilent(@NotNull Entity entity) {
        entities.remove(entity);
    }

    public void beforeUpdate() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.beforeUpdate();
    }
    public void update() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.update();
    }
    public void afterUpdate() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.afterUpdate();
    }

    public void beforeRender() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.beforeRender();
    }
    public void render() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.render();
    }
    public void afterRender() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.afterRender();
    }

    public void beforeDebugRender() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.beforeDebugRender();
    }
    public void debugRender() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.debugRender();
    }
    public void afterDebugRender() {
        for (Entity entity : entities)
            if (entity.isActive()) entity.afterDebugRender();
    }
}