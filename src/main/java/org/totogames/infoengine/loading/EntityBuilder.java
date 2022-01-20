package org.totogames.infoengine.loading;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.totogames.infoengine.ecs.Entity;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class EntityBuilder {
    private static final Map<String, Class<? extends Entity>> cache = new HashMap<>();

    private Entity parent = null;
    private Vector3f position = new Vector3f();
    private Quaternionf rotation = new Quaternionf();
    private Map<String, Object> fieldOverrides = new HashMap<>();

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
    public @NotNull EntityBuilder setFieldOverrides(@NotNull Map<String, Object> fieldOverrides) {
        this.fieldOverrides = fieldOverrides;
        return this;
    }
    public @NotNull EntityBuilder addFieldOverride(@NotNull String fieldName, Object object) {
        fieldOverrides.put(fieldName, object);
        return this;
    }

    public Entity build(@NotNull String typeName) {
        if (!cache.containsKey(typeName)) {
            try {
                cache.put(typeName, (Class<? extends Entity>) Class.forName(typeName));
            } catch (ClassNotFoundException e) {
                Logger.log(LogSeverity.Critical, "EntityBuilder", "Class <" + typeName + "> could not be found");
                return null;
            } catch (ClassCastException e) {
                Logger.log(LogSeverity.Critical, "EntityBuilder", "Class <" + typeName + "> does not extend Entity");
                return null;
            }
        }

        return build(cache.get(typeName));
    }

    public <T extends Entity> T build(@NotNull Class<T> type) {
        T entity;

        try {
            entity = type.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Logger.log(LogSeverity.Critical, "EntityBuilder", "Error while initializing Entity: Class <" + type.getName() + "> could not be instantiated");
            return null;
        }

        // Set fields
        for (Map.Entry<String, Object> override : fieldOverrides.entrySet()) {
            try {
                Field field = type.getDeclaredField(override.getKey());
                field.setAccessible(true);
                field.set(entity, override.getValue());
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                Logger.log(LogSeverity.Error, "EntityBuilder", "Error while initializing Entity: Field <" + override.getKey() + "> could not be set");
            }
        }

        entity.setParent(parent);
        entity.setPosition(position);
        entity.setRotation(rotation);

        entity.initialized();
        Logger.log(LogSeverity.Debug, "EntityBuilder", "Entity instantiated and initialized"); //TODO: Better log

        // Reset build args
        parent = null;
        position = new Vector3f();
        rotation = new Quaternionf();
        fieldOverrides.clear();
        return entity;
    }
}
