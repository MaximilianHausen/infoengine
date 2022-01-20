package org.totogames.infoengine.tests.loading;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.totogames.infoengine.ecs.Entity;
import org.totogames.infoengine.loading.EntityBuilder;
import org.totogames.infoengine.tests.CamelCaseGenerator;

@DisplayNameGeneration(CamelCaseGenerator.class)
public class EntityBuilderTests {
    @Test
    public void emptyBuild() {
        TestEntity classEntity = new EntityBuilder().build(TestEntity.class);
        TestEntity stringEntity = (TestEntity) new EntityBuilder().build(TestEntity.class.getName());
        Assertions.assertAll(
                () -> Assertions.assertNotNull(classEntity),
                () -> Assertions.assertNotNull(stringEntity)
        );
    }

    @Test
    public void fullDataSet() {
        TestEntity parent = new TestEntity();
        TestEntity entity = new EntityBuilder()
                .setPosition(new Vector3f(1, 2, 3))
                .setRotation(new Quaternionf(1, 2, 3, 4))
                .addFieldOverride("testField", "TestString")
                .setParent(parent)
                .build(TestEntity.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(new Vector3f(1, 2, 3), entity.getPostion()),
                () -> Assertions.assertEquals(new Quaternionf(1, 2, 3, 4), entity.getRotation()),
                () -> Assertions.assertEquals("TestString", entity.testField),
                () -> Assertions.assertEquals(parent, entity.getParent()),
                () -> Assertions.assertTrue(parent.getChildren().contains(entity))
        );
    }

    public static class TestEntity extends Entity {
        private String testField;

        @Override
        public void initialized() {

        }
    }
}
