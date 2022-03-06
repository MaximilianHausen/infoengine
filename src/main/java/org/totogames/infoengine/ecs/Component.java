package org.totogames.infoengine.ecs;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

public abstract class Component {
    private Entity entity;
    private boolean active;

    public abstract void initialized();

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

    void beforeUpdate() {
    }
    void update() {
    }
    void afterUpdate() {
    }

    void beforeRender() {
    }
    void render() {
    }
    void afterRender() {
    }

    void beforeDebugRender() {
    }
    void debugRender() {
    }
    void afterDebugRender() {
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
