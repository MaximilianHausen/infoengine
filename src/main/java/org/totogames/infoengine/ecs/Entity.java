package org.totogames.infoengine.ecs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Entity {
    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();

    private final LinkedList<Component> components = new LinkedList<>();
    private final List<Entity> children = new LinkedList<>();

    private Entity parent;
    private Scene scene;
    private boolean active = true;

    public @Nullable Scene getScene() {
        return scene;
    }

    public @NotNull Vector3f getWorldPosition() {
        if (parent == null) return new Vector3f(position);
        return parent.getWorldPosition().add(position);
    }
    public @NotNull Vector3f getPosition() {
        return position;
    }
    public void setPosition(@NotNull Vector3f position) {
        this.position.set(position);
    }

    public @NotNull Quaternionf getWorldRotation() {
        if (parent == null) return new Quaternionf(rotation);
        return parent.getWorldRotation().mul(rotation);
    }
    public @NotNull Quaternionf getRotation() {
        return rotation;
    }
    public void setRotation(@NotNull Quaternionf rotation) {
        this.rotation.set(rotation);
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public @NotNull List<Entity> getHierarchy() {
        if (parent == null) {
            List<Entity> temp = new LinkedList<>();
            temp.add(this);
            return temp;
        }
        List<Entity> temp = parent.getHierarchy();
        temp.add(this);
        return temp;
    }
    public @Nullable Entity getParent() {
        return parent;
    }
    public void setParent(@Nullable Entity parent) {
        if (this.parent == parent) return;
        if (this.parent != null)
            this.parent.children.remove(this);
        if (parent != null)
            parent.children.add(this);
        this.parent = parent;
    }

    public @UnmodifiableView @NotNull List<Entity> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(@NotNull Entity child) {
        child.setParent(this);
    }
    public void removeChild(@NotNull Entity child) {
        child.setParent(null);
    }

    public <T extends Component> @Nullable T getComponent(@NotNull Class<T> type) {
        return (T) components.stream().filter(c -> c.getClass().equals(type)).findFirst().orElse(null);
    }

    public void addComponent(@NotNull Component component) {
        components.add(component);
        component.added(this);
    }
    public void removeComponent(@NotNull Component component) {
        components.remove(component);
        component.removed(this);
    }

    public void removeSelf() {
        if (scene == null) {
            Logger.log(LogSeverity.Error, "Entity", "Error while removing a entity: The entity is not attached to any scene");
            return;
        }

        setParent(null);
        scene.remove(this);
    }

    void added(@NotNull Scene scene) {
        this.scene = scene;
    }
    void removed(@NotNull Scene scene) {
        this.scene = null;
    }

    public void beforeUpdate() {
        for (Component component : components)
            if (component.isActive()) component.beforeUpdate();
        for (Entity child : children)
            if (child.isActive()) child.beforeUpdate();
    }
    public void update() {
        for (Component component : components)
            if (component.isActive()) component.update();
        for (Entity child : children)
            if (child.isActive()) child.update();
    }
    public void afterUpdate() {
        for (Component component : components)
            if (component.isActive()) component.afterUpdate();
        for (Entity child : children)
            if (child.isActive()) child.afterUpdate();
    }

    public void beforeRender() {
        for (Component component : components)
            if (component.isActive()) component.beforeRender();
        for (Entity child : children)
            if (child.isActive()) child.beforeRender();
    }
    public void render() {
        for (Component component : components)
            if (component.isActive()) component.render();
        for (Entity child : children)
            if (child.isActive()) child.render();
    }
    public void afterRender() {
        for (Component component : components)
            if (component.isActive()) component.afterRender();
        for (Entity child : children)
            if (child.isActive()) child.afterRender();
    }

    public void beforeDebugRender() {
        for (Component component : components)
            if (component.isActive()) component.beforeDebugRender();
        for (Entity child : children)
            if (child.isActive()) child.beforeDebugRender();
    }
    public void debugRender() {
        for (Component component : components)
            if (component.isActive()) component.debugRender();
        for (Entity child : children)
            if (child.isActive()) child.debugRender();
    }
    public void afterDebugRender() {
        for (Component component : components)
            if (component.isActive()) component.afterDebugRender();
        for (Entity child : children)
            if (child.isActive()) child.afterDebugRender();
    }
}
