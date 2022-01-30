package org.totogames.infoengine.loading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.totogames.infoengine.ecs.Entity;
import org.totogames.infoengine.ecs.Scene;
import org.totogames.infoengine.util.IO;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.io.File;
import java.nio.file.Path;

public class SceneLoader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public @NotNull Scene loadSceneFromFile(@NotNull Path path) {
        return loadScene(IO.getTextFromFile(new File(path.toUri())));
    }

    public @NotNull Scene loadScene(@NotNull String sceneJson) {
        SceneModel sceneModel;
        Scene scene = new Scene();

        if (sceneJson.equals("")) {
            Logger.log(LogSeverity.Critical, "SceneLoader", "File not found or is empty");
            return scene;
        }

        try {
            sceneModel = gson.fromJson(sceneJson, SceneModel.class);
        } catch (JsonSyntaxException e) {
            Logger.log(LogSeverity.Critical, "SceneLoader", "Invalid scene file");
            return scene;
        }

        // Load Scene
        for (EntityModel entityModel : sceneModel.entities) {
            addToSceneRecursive(loadEntity(entityModel, null), scene);
        }

        Logger.log(LogSeverity.Critical, "SceneLoader", "Scene <" + sceneModel.name + "> loaded");
        return scene;
    }

    public Entity loadEntity(@NotNull EntityModel entityModel, @Nullable Entity parent) {
        Entity entity = new EntityBuilder()
                .setParent(parent)
                .setFieldOverrides(entityModel.data)
                .setPosition(new Vector3f(entityModel.x, entityModel.y, entityModel.z)) // TODO: Set rotation
                .build(entityModel.type);

        for (EntityModel child : entityModel.children)
            loadEntity(child, entity);

        return entity;
    }

    private void addToSceneRecursive(@NotNull Entity entity, @NotNull Scene scene) {
        scene.add(entity);
        for (Entity child : entity.getChildren()) {
            addToSceneRecursive(child, scene);
        }
    }
}