package org.totodev.engine.resources.scene;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.totodev.engine.ecs.*;
import org.totodev.engine.util.IO;
import org.totodev.engine.util.logging.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class SceneLoader {
    private static final Gson gson = new GsonBuilder().create();

    /**
     * Loads a scene from a file
     * @param path The path to the file
     * @return The loaded scene
     */
    public static @NotNull Scene loadSceneFromFile(@NotNull Path path) {
        String jsonText = IO.getTextFromFile(new File(path.toUri()));
        if (jsonText.equals("")) {
            Logger.log(LogLevel.Critical, "SceneLoader", "File " + path + " not found or is empty");
            return new Scene();
        }
        return loadScene(jsonText);
    }

    /**
     * Loads a scene from json text
     * @param sceneJson The json text
     * @return The loaded scene
     */
    public static @NotNull Scene loadScene(@NotNull String sceneJson) {
        SceneModel sceneModel;
        Scene scene = new Scene();
        int errors = 0;

        // Deserialize json
        try {
            sceneModel = gson.fromJson(sceneJson, SceneModel.class);
        } catch (JsonSyntaxException e) {
            Logger.log(LogLevel.Critical, "SceneLoader", "Invalid scene file");
            return scene;
        }

        // Create entities
        for (int i = 0; i < sceneModel.entityCount; i++)
            scene.createEntity();

        // Register and initialize components
        for (ComponentModel componentModel : sceneModel.components) {
            try {
                Component component = (Component) Class.forName(componentModel.type).getDeclaredConstructor().newInstance();
                scene.addComponent(component);
                component.deserializeAllState(componentModel.data);
            } catch (ClassNotFoundException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Class " + componentModel.type + " could not be found. This component will not be added.");
                errors++;
            } catch (NoSuchMethodException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Empty constructor could not found on class " + componentModel.type + ". This component will not be added.");
                errors++;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Error while instantiating class " + componentModel.type + ". This component will not be added.");
                errors++;
            }
        }

        // Register and initialize global components
        //TODO: Reuse component objects as global components
        for (GlobalComponentModel componentModel : sceneModel.globalComponents) {
            try {
                GlobalComponent component = (GlobalComponent) Class.forName(componentModel.type).getDeclaredConstructor().newInstance();
                scene.addGlobalComponent(component);
                component.deserializeState(componentModel.data);
            } catch (ClassNotFoundException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Class " + componentModel.type + " could not be found. This global component will not be added.");
                errors++;
            } catch (NoSuchMethodException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Empty constructor could not found on class " + componentModel.type + ". This global component will not be added.");
                errors++;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Error while instantiating class " + componentModel.type + ". This global component will not be added.");
                errors++;
            }
        }

        // Add systems
        for (String type : sceneModel.systems) {
            try {
                BaseSystem system = (BaseSystem) Class.forName(type).getDeclaredConstructor().newInstance();
                scene.addSystem(system);
            } catch (ClassNotFoundException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Class " + type + " could not be found. This system will not be added.");
                errors++;
            } catch (NoSuchMethodException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Empty constructor could not found on class " + type + ". This system will not be added.");
                errors++;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Logger.log(LogLevel.Error, "SceneLoader", "Error while instantiating class " + type + ". This system will not be added.");
                errors++;
            }
        }

        Logger.log(errors == 0 ? LogLevel.Info : LogLevel.Error, "SceneLoader", "Scene " + sceneModel.name + " loaded " + (errors == 0 ? "successfully." : "with " + errors + (errors == 1 ? " error." : " errors.")));
        scene.start();
        return scene;
    }
}
