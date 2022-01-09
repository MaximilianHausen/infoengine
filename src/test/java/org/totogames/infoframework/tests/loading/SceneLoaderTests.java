package org.totogames.infoframework.tests.loading;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.totogames.infoframework.ecs.Scene;
import org.totogames.infoframework.loading.SceneLoader;
import org.totogames.infoframework.util.IO;

import java.io.File;

public class SceneLoaderTests {
    @Test
    public void loadEmpty() {
        SceneLoader sceneLoader = new SceneLoader();
        File testSceneFile = IO.getFileFromResource("json/emptyScene.json");
        Scene scene = sceneLoader.loadSceneFromFile(testSceneFile.toPath());

        Assertions.assertNotNull(scene);
        Assertions.assertTrue(scene.getEntities().isEmpty());
    }
}
