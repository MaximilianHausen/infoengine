package net.totodev.infoengine.loading;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for instantiating components. Can be reused, all parameters reset after each build.
 */
public class ComponentBuilder {
    private static final Map<String, Class<? extends Component>> cache = new HashMap<>();

    private Map<String, Object> fieldOverrides = new HashMap<>();

    public @NotNull ComponentBuilder setFieldOverrides(@NotNull Map<String, Object> fieldOverrides) {
        this.fieldOverrides = fieldOverrides;
        return this;
    }
    public @NotNull ComponentBuilder addFieldOverride(@NotNull String fieldName, Object object) {
        fieldOverrides.put(fieldName, object);
        return this;
    }

    public Component build(@NotNull String typeName) {
        if (!cache.containsKey(typeName)) {
            try {
                cache.put(typeName, (Class<? extends Component>) Class.forName(typeName));
            } catch (ClassNotFoundException e) {
                Logger.log(LogLevel.Critical, "ComponentBuilder", "Class <" + typeName + "> could not be found");
                return null;
            } catch (ClassCastException e) {
                Logger.log(LogLevel.Critical, "ComponentBuilder", "Class <" + typeName + "> does not extend Component");
                return null;
            }
        }

        return build(cache.get(typeName));
    }

    public <T extends Component> T build(@NotNull Class<T> type) {
        T component;

        try {
            component = type.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Logger.log(LogLevel.Error, "ComponentBuilder", "Error while initializing Component: Class <" + type.getName() + "> could not be instantiated");
            return null;
        }

        // Set fields
        for (Map.Entry<String, Object> override : fieldOverrides.entrySet()) {
            try {
                Field field = type.getDeclaredField(override.getKey());
                field.setAccessible(true);
                field.set(component, override.getValue());
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                Logger.log(LogLevel.Error, "ComponentBuilder", "Error while initializing Component: Field <" + override.getKey() + "> could not be set");
            }
        }

        component.initialized();
        Logger.log(LogLevel.Debug, "ComponentBuilder", "Component instantiated and initialized"); //TODO: Better log

        // Reset build args
        fieldOverrides.clear();
        return component;
    }
}
