package org.totogames.infoengine.ecs;

import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

public abstract class Component {
    private Entity entity;
    private boolean active;
    private boolean visible;

    public abstract void initialized();

    public Entity getEntity() {
        return entity;
    }

    public void added(Entity entity) {
        this.entity = entity;
    }
    public void removed(Entity entity) {
        this.entity = null;
    }

    public void removeSelf() {
        if (entity == null) {
            Logger.log(LogSeverity.Error, "Component", "Error while removing a component: The component is not attached to any entity");
            return;
        }

        entity.remove(this);
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

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
