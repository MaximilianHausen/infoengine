package net.totodev.infoengine.ecs;

import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public abstract class Component {
    private Entity entity;
    private boolean active = true;

    public void initialized() {
    }

    public Entity getEntity() {
        return entity;
    }

    @MustBeInvokedByOverriders
    public void added(@NotNull Entity entity) {
        this.entity = entity;
    }
    @MustBeInvokedByOverriders
    public void removed(@NotNull Entity entity) {
        this.entity = null;
    }

    public void removeSelf() {
        if (entity == null) {
            Logger.log(LogSeverity.Error, "Component", "Error while removing a component: The component is not attached to any entity");
            return;
        }

        entity.removeComponent(this);
    }

    public void beforeUpdate() {
    }
    public void update() {
    }
    public void afterUpdate() {
    }

    public void beforeRender() {
    }
    public void render() {
    }
    public void afterRender() {
    }

    public void beforeDebugRender() {
    }
    public void debugRender() {
    }
    public void afterDebugRender() {
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
