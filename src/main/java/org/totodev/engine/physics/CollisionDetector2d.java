package org.totodev.engine.physics;

import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.joml.Vector2f;
import org.totodev.engine.core.components.Transform2d;
import org.totodev.engine.core.systems.Updater;
import org.totodev.engine.ecs.*;

public class CollisionDetector2d extends BaseSystem {
    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private BoxCollider2d collider;

    @EventSubscriber(Updater.EVENT_POST_UPDATE)
    public void update(float deltaTime) {
        MutableIntList toCheck = getScene().getEntitiesByComponents(BoxCollider2d.class, Transform2d.class);
        toCheck.forEach(e1 -> toCheck.forEach(e2 -> {
            int layer = collider.getLayer(e1);
            if (e1 == e2 || layer != collider.getLayer(e2)) return;
            if (checkCollision(e1, e2)) getScene().events
                    .invokeEvent("PhysStay", layer, e1, collider.getType(e1), e2, collider.getType(e2));
        }));
    }

    private boolean checkCollision(int entity1, int entity2) {
        // Adapted from https://developer.ibm.com/tutorials/wa-build2dphysicsengine
        Vector2f pos = transform.getPosition(entity1, new Vector2f()).add(collider.getOffset(entity1, new Vector2f()));
        Vector2f size = collider.getSize(entity1, new Vector2f());
        float width = size.x / 2;
        float height = size.y / 2;

        float l1 = pos.x - width;
        float t1 = pos.y + height;
        float r1 = pos.x + width;
        float b1 = pos.y - height;

        pos = transform.getPosition(entity2, new Vector2f()).add(collider.getOffset(entity2, new Vector2f()));
        size = collider.getSize(entity2, new Vector2f());
        width = size.x / 2;
        height = size.y / 2;

        float l2 = pos.x - width;
        float t2 = pos.y + height;
        float r2 = pos.x + width;
        float b2 = pos.y - height;

        return !(b1 > t2 || b2 > t1 || r1 < l2 || l1 > r2);
    }
}
