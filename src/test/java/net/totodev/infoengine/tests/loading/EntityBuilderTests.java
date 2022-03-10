package net.totodev.infoengine.tests.loading;

import net.totodev.infoengine.ecs.Entity;
import net.totodev.infoengine.loading.EntityBuilder;
import net.totodev.infoengine.tests.CamelCaseGenerator;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class EntityBuilderTests {
    @Test
    public void emptyBuild() {
        Entity classEntity = new EntityBuilder().build();
        Assertions.assertNotNull(classEntity);
    }

    @Test
    public void fullDataSet() {
        Entity parent = new Entity();
        Entity entity = new EntityBuilder()
                .setPosition(new Vector3f(1, 2, 3))
                .setRotation(new Quaternionf(1, 2, 3, 4))
                .setParent(parent)
                .build();
        Assertions.assertAll(
                () -> Assertions.assertEquals(new Vector3f(1, 2, 3), entity.getPosition()),
                () -> Assertions.assertEquals(new Quaternionf(1, 2, 3, 4), entity.getRotation()),
                () -> Assertions.assertEquals(parent, entity.getParent()),
                () -> Assertions.assertTrue(parent.getChildren().contains(entity))
        );
    }
}
