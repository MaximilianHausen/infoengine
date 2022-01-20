package org.totogames.infoengine.ecs;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
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

public abstract class Entity {
    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();

    private final LinkedList<Component> components = new LinkedList<>();
    private final List<Entity> children = new LinkedList<>();

    private Entity parent;
    private Scene scene;

    public abstract void initialized();

    public Scene getScene() {
        return scene;
    }

    public @NotNull Vector3f getWorldPosition() {
        if (parent == null) return new Vector3f(position);

        return parent.getWorldPosition().add(position);
    }
    public @NotNull Vector3f getPostion() {
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
    public Entity getParent() {
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

    @MustBeInvokedByOverriders
    public void added(@NotNull Scene scene) {
        this.scene = scene;
    }
    @MustBeInvokedByOverriders
    public void removed(@NotNull Scene scene) {
        this.scene = null;
    }

    public void addChild(@NotNull Entity child) {
        child.setParent(this);
    }
    public void removeChild(@NotNull Entity child) {
        child.setParent(null);
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

    @MustBeInvokedByOverriders
    public void beforeUpdate() {
        for (Component component : components) {
            if (component.isActive()) component.beforeUpdate();
        }
    }
    @MustBeInvokedByOverriders
    public void update() {
        for (Component component : components) {
            if (component.isActive()) component.update();
        }
    }
    @MustBeInvokedByOverriders
    public void afterUpdate() {
        for (Component component : components) {
            if (component.isActive()) component.afterUpdate();
        }
    }

    @MustBeInvokedByOverriders
    public void beforeRender() {
        for (Component component : components) {
            if (component.isVisible()) component.beforeRender();
        }
    }
    @MustBeInvokedByOverriders
    public void render() {
        for (Component component : components) {
            if (component.isVisible()) component.render();
        }
    }
    @MustBeInvokedByOverriders
    public void afterRender() {
        for (Component component : components) {
            if (component.isVisible()) component.afterRender();
        }
    }
}
