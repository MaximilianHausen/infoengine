package org.totogames.infoengine.ecs;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Entity {
    private Vector3f position;
    private Quaternionf rotation;

    private final LinkedList<Component> components = new LinkedList<>();
    private final List<Entity> children = new LinkedList<>();

    private Entity parent;
    private Scene scene;

    public abstract void initialized();

    public Scene getScene() {
        return scene;
    }

    public Vector3f getWorldPosition() {
        if (parent == null) return new Vector3f(position);

        return parent.getWorldPosition().add(position);
    }
    public Vector3f getPostion() {
        return position;
    }
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getWorldRotation() {
        if (parent == null) return new Quaternionf(rotation);

        return parent.getWorldRotation().mul(rotation);
    }
    public Quaternionf getRotation() {
        return rotation;
    }
    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public List<Entity> getHierarchy() {
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
    public void setParent(Entity parent) {
        if (this.parent != null)
            this.parent.children.remove(this);
        if (parent != null)
            parent.children.add(this);
        this.parent = parent;
    }

    /**
     * @return A read-only list of all the children
     */
    public List<Entity> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void added(Scene scene) {
        this.scene = scene;
    }
    public void removed(Scene scene) {
        this.scene = null;
    }

    public void add(Component component) {
        components.add(component);
        component.added(this);
    }
    public void remove(Component component) {
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

    public void beforeUpdate() {
        for (Component component : components) {
            if (component.isActive()) component.beforeUpdate();
        }
    }
    public void update() {
        for (Component component : components) {
            if (component.isActive()) component.update();
        }
    }
    public void afterUpdate() {
        for (Component component : components) {
            if (component.isActive()) component.afterUpdate();
        }
    }

    public void beforeRender() {
        for (Component component : components) {
            if (component.isVisible()) component.beforeRender();
        }
    }
    public void render() {
        for (Component component : components) {
            if (component.isVisible()) component.render();
        }
    }
    public void afterRender() {
        for (Component component : components) {
            if (component.isVisible()) component.afterRender();
        }
    }
}
