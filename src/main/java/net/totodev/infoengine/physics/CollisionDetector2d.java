package net.totodev.infoengine.physics;

import net.totodev.infoengine.core.CoreEvents;
import net.totodev.infoengine.core.components.Transform2d;
import net.totodev.infoengine.ecs.*;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.joml.Vector2f;

public class CollisionDetector2d extends BaseSystem {
    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private BoxCollider2d collider;

    @EventSubscriber(CoreEvents.Update)
    public void update() {
        MutableIntList toCheck = getScene().getEntitiesByComponents(BoxCollider2d.class, Transform2d.class);
        //TODO: Don't check from both sides
        toCheck.forEach(e1 -> toCheck.forEach(e2 -> {
            if (e1 == e2) return;
            if (checkCollision(e1, e2)) getScene().events.invokeEvent("Phys:" + collider.getCollisionType(e1), e1, e2);
        }));
    }

    private boolean checkCollision(int entity1, int entity2) {
        Vector2f pos = transform.getPosition(entity1, new Vector2f()).add(collider.getOffset(entity1));
        Vector2f size = collider.getSize(entity1);
        float width = size.x / 2, height = size.y / 2;

        float l1 = pos.x - width;
        float t1 = pos.y + height;
        float r1 = pos.x + width;
        float b1 = pos.y - height;

        pos = transform.getPosition(entity2, new Vector2f()).add(collider.getOffset(entity2));
        size = collider.getSize(entity2);
        width = size.x / 2;
        height = size.y / 2;

        float l2 = pos.x - width;
        float t2 = pos.y + height;
        float r2 = pos.x + width;
        float b2 = pos.y - height;

        return b1 < t2 || t1 > b2 || r1 < l2 || l1 > r2;
    }
}
