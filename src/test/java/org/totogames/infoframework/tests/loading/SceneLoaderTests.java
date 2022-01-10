package org.totogames.infoframework.tests.loading;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.totogames.infoframework.ecs.Entity;
import org.totogames.infoframework.ecs.Scene;
import org.totogames.infoframework.loading.SceneLoader;
import org.totogames.infoframework.util.IO;

import java.io.File;

public class SceneLoaderTests {
    public static class TestEntity extends Entity {
        private String testField;

        @Override public void initialized() {

        }
    }

    @Test
    @DisplayName("LoadEmpty")
    public void loadEmpty() {
        SceneLoader sceneLoader = new SceneLoader();
        File testSceneFile = IO.getFileFromResource("json/emptyScene.json");
        Scene scene = sceneLoader.loadSceneFromFile(testSceneFile.toPath());

        Assertions.assertNotNull(scene);
        Assertions.assertTrue(scene.getEntities().isEmpty());
    }

    @Test
    @DisplayName("Load")
    public void load() {
        SceneLoader sceneLoader = new SceneLoader();
        File testSceneFile = IO.getFileFromResource("json/sceneLoaderTestScene.json");
        Scene scene = sceneLoader.loadSceneFromFile(testSceneFile.toPath());

        Assertions.assertNotNull(scene);
        Assertions.assertEquals(3, scene.getEntities().size());

        TestEntity entity1 = (TestEntity) scene.getEntities().stream().filter(e -> e.getParent() == null && e.getChildren().isEmpty()).toArray()[0];
        TestEntity entity2 = (TestEntity) scene.getEntities().stream().filter(e -> e.getParent() == null && !e.getChildren().isEmpty()).toArray()[0];
        TestEntity entity3 = (TestEntity) scene.getEntities().stream().filter(e -> e.getParent() != null && e.getChildren().isEmpty()).toArray()[0];

        Assertions.assertAll(
                () -> Assertions.assertEquals(new Vector3f(), entity1.getPostion()),
                () -> Assertions.assertEquals(new Vector3f(1), entity2.getPostion()),
                () -> Assertions.assertEquals(new Vector3f(), entity3.getPostion()),
                () -> Assertions.assertEquals(new Quaternionf(), entity1.getRotation()),
                () -> Assertions.assertEquals(new Quaternionf(), entity2.getRotation()),
                () -> Assertions.assertEquals(new Quaternionf(), entity3.getRotation()),
                () -> Assertions.assertEquals("TestValue", entity1.testField),
                () -> Assertions.assertEquals(entity2, entity3.getParent()),
                () -> Assertions.assertTrue(entity2.getChildren().size() == 1 && entity2.getChildren().contains(entity3))
        );
    }
}
