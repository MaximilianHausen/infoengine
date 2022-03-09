package org.totogames.infoengine.ecs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Entity {
    private final Matrix4f transform = new Matrix4f();

    private final LinkedList<Component> components = new LinkedList<>();
    private final List<Entity> children = new LinkedList<>();

    private Entity parent;
    private Scene scene;
    private boolean active = true;

    public @Nullable Scene getScene() {
        return scene;
    }

    public @NotNull Matrix4f getTransform() {
        return transform;
    }
    public void setTransform(@NotNull Matrix4f transform) {
        this.transform.set(transform);
    }
    public @NotNull Matrix4f getWorldTransform() {
        if (parent == null) return new Matrix4f().set(transform);
        return transform.mul(parent.getWorldTransform());
    }

    public @NotNull Vector3f getPosition() {
        return transform.getTranslation(new Vector3f());
    }
    public void setPosition(@NotNull Vector3f position) {
        transform.setTranslation(position);
    }
    public @NotNull Vector3f getWorldPosition() {
        if (parent == null) return getPosition();
        // TODO: This is really inefficient and probably wrong
        Vector4f temp = new Vector4f(parent.getWorldPosition(), 1).mul(transform);
        return new Vector3f(temp.x, temp.y, temp.z);
    }

    public @NotNull Quaternionf getRotation() {
        return null;
    }
    public void setRotation(@NotNull Quaternionf rotation) {
    }
    public @NotNull Quaternionf getWorldRotation() {
        if (parent == null) return getRotation();
        return getRotation().mul(parent.getWorldRotation());
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public @NotNull List<Entity> getAllParentEntities(@NotNull List<Entity> addTo) {
        addTo.add(this);
        if (parent != null) parent.getAllParentEntities(addTo);
        return addTo;
    }
    public @NotNull List<Entity> getAllChildEntities(@NotNull List<Entity> addTo) {
        addTo.add(this);
        for (Entity child : children)
            child.getAllChildEntities(addTo);
        return addTo;
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

        if (this.parent == null && parent != null && scene != null) {
            scene.removeSilent(this);
        }
        if (this.parent != null && parent == null && scene != null) {
            scene.addSilent(this);
        }

        if (parent != null && this.scene != parent.scene) {
            scene = parent.scene;
        }

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
