package net.totodev.infoengine.loading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.ecs.Entity;
import net.totodev.infoengine.ecs.Scene;
import net.totodev.infoengine.util.IO;
import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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
        Entity entity = new Entity();
        entity.setParent(parent);
        entity.setPosition(new Vector3f(entityModel.x, entityModel.y, entityModel.z));
        // TODO: Set rotation

        for (ComponentModel componentModel : entityModel.components)
            entity.addComponent(loadComponent(componentModel));
        for (EntityModel child : entityModel.children)
            loadEntity(child, entity);

        return entity;
    }

    public Component loadComponent(@NotNull ComponentModel componentModel) {
        return new ComponentBuilder()
                .setFieldOverrides(componentModel.data)
                .build(componentModel.type);
    }

    private void addToSceneRecursive(@NotNull Entity entity, @NotNull Scene scene) {
        scene.add(entity);
        for (Entity child : entity.getChildren()) {
            addToSceneRecursive(child, scene);
        }
    }
}
