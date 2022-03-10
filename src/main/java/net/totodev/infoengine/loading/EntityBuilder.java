package net.totodev.infoengine.loading;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.ecs.Entity;
import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for instantiating entities. Does nothing special, but can be nicer to use. Can be reused, all parameters reset after each build.
 */
public class EntityBuilder {
    private Entity parent = null;
    private Vector3f position = new Vector3f();
    private Quaternionf rotation = new Quaternionf();
    private List<Component> components = new LinkedList<>();

    public @NotNull EntityBuilder setParent(@Nullable Entity parent) {
        this.parent = parent;
        return this;
    }
    public @NotNull EntityBuilder setPosition(@NotNull Vector3f position) {
        this.position = position;
        return this;
    }
    public @NotNull EntityBuilder setRotation(@NotNull Quaternionf rotation) {
        this.rotation = rotation;
        return this;
    }
    public @NotNull EntityBuilder setComponents(@NotNull Component... components) {
        this.components = Arrays.stream(components).toList();
        return this;
    }
    public @NotNull EntityBuilder addComponent(@NotNull Component component) {
        components.add(component);
        return this;
    }

    public Entity build() {
        Entity entity = new Entity();

        entity.setParent(parent);
        entity.setPosition(position);
        entity.setRotation(rotation);
        components.forEach(entity::addComponent);

        //entity.initialized();
        Logger.log(LogSeverity.Debug, "EntityBuilder", "Entity instantiated and initialized"); //TODO: Better log

        // Reset build args
        parent = null;
        position = new Vector3f();
        rotation = new Quaternionf();
        return entity;
    }
}
