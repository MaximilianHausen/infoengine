package org.totogames.infoframework.loading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.joml.Vector3f;
import org.totogames.infoframework.ecs.Entity;
import org.totogames.infoframework.ecs.Scene;
import org.totogames.infoframework.util.IO;
import org.totogames.infoframework.util.logging.LogSeverity;
import org.totogames.infoframework.util.logging.Logger;

import java.io.File;
import java.nio.file.Path;

public class SceneLoader {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Scene loadSceneFromFile(Path path) {
        return loadScene(IO.getTextFromFile(new File(path.toUri())));
    }

    public Scene loadScene(String sceneJson) {
        SceneModel sceneModel;
        Scene scene = new Scene();

        if (sceneJson.equals("")) {
            Logger.log(LogSeverity.Critical, "SceneLoader", "File not found or is empty");
            return null;
        }

        try {
            sceneModel = gson.fromJson(sceneJson, SceneModel.class);
        } catch (JsonSyntaxException e) {
            Logger.log(LogSeverity.Critical, "SceneLoader", "Invalid scene file");
            return null;
        }

        // Load Scene
        for (EntityModel entityModel : sceneModel.entities) {
            addToSceneRecursive(loadEntity(entityModel, null), scene);
        }

        Logger.log(LogSeverity.Critical, "SceneLoader", "Scene <" + sceneModel.name + "> loaded");
        return scene;
    }

    public Entity loadEntity(EntityModel entityModel, Entity parent) {
        Entity entity = new EntityBuilder()
                .setParent(parent)
                .setFieldOverrides(entityModel.data)
                .setPosition(new Vector3f(entityModel.x, entityModel.y, entityModel.z)) // TODO: Set rotation
                .build(entityModel.type);

        for (EntityModel child : entityModel.children)
            loadEntity(child, entity);

        return entity;
    }

    private void addToSceneRecursive(Entity entity, Scene scene) {
        scene.add(entity);
        for (Entity child : entity.getChildren()) {
            addToSceneRecursive(child, scene);
        }
    }
}
