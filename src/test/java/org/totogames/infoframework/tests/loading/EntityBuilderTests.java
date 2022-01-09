package org.totogames.infoframework.tests.loading;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.totogames.infoframework.ecs.Entity;
import org.totogames.infoframework.loading.EntityBuilder;

public class EntityBuilderTests {
    public static class TestEntity extends Entity {
        private String testField;
        @Override public void initialized() {

        }
    }

    @Test
    public void simpleBuild() {
        TestEntity classEntity = new EntityBuilder().build(TestEntity.class);
        TestEntity stringEntity = (TestEntity) new EntityBuilder().build(TestEntity.class.getName());
        Assertions.assertAll(
                () -> Assertions.assertNotNull(classEntity),
                () -> Assertions.assertNotNull(stringEntity)
        );
    }

    @Test
    public void fieldOverrideTest() {
        TestEntity entity = new EntityBuilder()
                .addFieldOverride("testField", "TestString")
                .build(TestEntity.class);
        Assertions.assertEquals("TestString", entity.testField);
    }
}
