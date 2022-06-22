package net.totodev.infoengine.tests.loading;

import net.totodev.infoengine.core.components.Transform3d;
import net.totodev.infoengine.ecs.Scene;
import net.totodev.infoengine.resources.scene.SceneLoader;
import net.totodev.infoengine.tests.CamelCaseGenerator;
import net.totodev.infoengine.util.IO;
import org.joml.*;
import org.junit.jupiter.api.*;

import java.io.File;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class SceneLoaderTests {
    @Test
    public void loadEmpty() {
        File testSceneFile = IO.getFileFromResource("json/emptyScene.json", false);
        if (testSceneFile == null)
            Assertions.fail("File not found in resources/json/emptyScene.json");

        Scene scene = SceneLoader.loadSceneFromFile(testSceneFile.toPath());
        Assertions.assertNotNull(scene);
        Assertions.assertTrue(scene.getAllEntities().isEmpty());
    }

    @Test
    public void load() {
        File testSceneFile = IO.getFileFromResource("json/simpleTestScene.json", false);
        if (testSceneFile == null)
            Assertions.fail("File not found in resources/json/simpleTestScene.json");

        Scene scene = SceneLoader.loadSceneFromFile(testSceneFile.toPath());
        Transform3d transform = scene.getComponent(Transform3d.class);

        Assertions.assertEquals(1, scene.getAllEntities().size());

        Assertions.assertAll(
                () -> Assertions.assertEquals(new Vector3f().add(1, 2, 3), transform.getPosition(0, new Vector3f())),
                () -> Assertions.assertEquals(new Quaternionf(), transform.getRotation(0, new Quaternionf()))
        );
    }
}
