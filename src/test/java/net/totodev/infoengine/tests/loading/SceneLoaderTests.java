package net.totodev.infoengine.tests.loading;

import net.totodev.infoengine.ecs.Component;
import net.totodev.infoengine.ecs.Entity;
import net.totodev.infoengine.ecs.Scene;
import net.totodev.infoengine.loading.SceneLoader;
import net.totodev.infoengine.tests.CamelCaseGenerator;
import net.totodev.infoengine.util.IO;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.io.File;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class SceneLoaderTests {
    @Test
    public void loadEmpty() {
        SceneLoader sceneLoader = new SceneLoader();
        File testSceneFile = IO.getFileFromResource("json/emptyScene.json");
        Scene scene = sceneLoader.loadSceneFromFile(testSceneFile.toPath());

        Assertions.assertNotNull(scene);
        Assertions.assertTrue(scene.getEntities().isEmpty());
    }

    @Test
    public void load() {
        SceneLoader sceneLoader = new SceneLoader();
        File testSceneFile = IO.getFileFromResource("json/sceneLoaderTestScene.json");
        Scene scene = sceneLoader.loadSceneFromFile(testSceneFile.toPath());

        Assertions.assertNotNull(scene);
        Assertions.assertEquals(3, scene.getEntities().size());

        Entity entity1 = (Entity) scene.getEntities().stream().filter(e -> e.getParent() == null && e.getChildren().isEmpty()).toArray()[0];
        Entity entity2 = (Entity) scene.getEntities().stream().filter(e -> e.getParent() == null && !e.getChildren().isEmpty()).toArray()[0];
        Entity entity3 = (Entity) scene.getEntities().stream().filter(e -> e.getParent() != null && e.getChildren().isEmpty()).toArray()[0];

        Assertions.assertAll(
                () -> Assertions.assertEquals(new Vector3f(), entity1.getPosition()),
                () -> Assertions.assertEquals(new Vector3f(1), entity2.getPosition()),
                () -> Assertions.assertEquals(new Vector3f(), entity3.getPosition()),
                () -> Assertions.assertEquals(new Quaternionf(), entity1.getRotation()),
                () -> Assertions.assertEquals(new Quaternionf(), entity2.getRotation()),
                () -> Assertions.assertEquals(new Quaternionf(), entity3.getRotation()),
                () -> Assertions.assertEquals("TestValue", entity1.getComponent(TestComponent.class).testField),
                () -> Assertions.assertEquals(entity2, entity3.getParent()),
                () -> Assertions.assertTrue(entity2.getChildren().size() == 1 && entity2.getChildren().contains(entity3))
        );
    }

    public static class TestComponent extends Component {
        private String testField;

        @Override
        public void initialized() {

        }
    }
}
