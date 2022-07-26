package net.totodev.engine.physics;

import net.totodev.engine.core.components.Transform2d;
import net.totodev.engine.ecs.*;
import org.joml.Vector2f;

public class CollisionSolver2d extends BaseSystem {
    public static final int COLL_TYPE_SOLVE_KINETIC = 0;
    public static final int COLL_TYPE_SOLVE_DYNAMIC = 1;
    private static final float STICKY_THRESHOLD = 0.01f;

    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private BoxCollider2d collider;
    @CachedComponent
    private Velocity2d velocity;

    @EventSubscriber("PhysStay")
    public void solveCollision(int layer, int entity1, int type1, int entity2, int type2) {
        if (type1 != COLL_TYPE_SOLVE_DYNAMIC || type2 != COLL_TYPE_SOLVE_KINETIC) return;

        // Adapted from https://developer.ibm.com/tutorials/wa-build2dphysicsengine
        Vector2f pos1 = transform.getPosition(entity1, new Vector2f()).add(collider.getOffset(entity1, new Vector2f()));
        Vector2f pos2 = transform.getPosition(entity2, new Vector2f()).add(collider.getOffset(entity2, new Vector2f()));

        Vector2f size1 = collider.getSize(entity1, new Vector2f());
        Vector2f size2 = collider.getSize(entity2, new Vector2f());

        Vector2f vel1 = velocity.getVelocity(entity1, new Vector2f());
        Vector2f vel2 = velocity.getVelocity(entity2, new Vector2f());

        float dX = (pos2.x - pos1.x) / (size2.x / 2);
        float dY = (pos2.y - pos1.y) / (size2.y / 2);

        float absDX = Math.abs(dX);
        float absDY = Math.abs(dY);

        // If the distance between the normalized x and y
        // position is less than a small threshold (.1 in this case)
        // then this object is approaching from a corner
        if (Math.abs(absDX - absDY) < .1) {
            if (dX < 0) {
                // If the object is approaching from positive X
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).add((size1.x + size2.x) / 2, 0));
            } else {
                // If the object is approaching from negative X
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).sub((size1.x + size2.x) / 2, 0));
            }

            if (dY < 0) {
                // If the object is approaching from positive Y
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).add(0, (size1.y + size2.y) / 2));
            } else {
                // If the object is approaching from negative Y
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).sub(0, (size1.y + size2.y) / 2));
            }

            // Randomly select a x/y direction to reflect velocity on
            if (Math.random() < .5) {

                // Reflect the velocity at a reduced rate
                velocity.setVelocity(entity1, vel1.mul(-collider.getRestitution(entity1), 1));

                // If the object's velocity is nearing 0, set it to 0
                // STICKY_THRESHOLD is set to .0004
                if (Math.abs(vel1.x) < STICKY_THRESHOLD)
                    velocity.setVelocity(entity1, 0, vel1.y);
            } else {
                velocity.setVelocity(entity1, vel1.mul(1, -collider.getRestitution(entity1)));

                if (Math.abs(vel1.y) < STICKY_THRESHOLD)
                    velocity.setVelocity(entity1, vel1.x, 0);
            }

        } else if (absDX > absDY) {
            // If the object is approaching from the sides

            if (dX < 0) {
                // If the object is approaching from positive X
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).add((size1.x + size2.x) / 2, 0));
            } else {
                // If the object is approaching from negative X
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).sub((size1.x + size2.x) / 2, 0));
            }

            // Velocity component
            velocity.setVelocity(entity1, vel1.mul(-collider.getRestitution(entity1), 1));

            if (Math.abs(vel1.x) < STICKY_THRESHOLD)
                velocity.setVelocity(entity1, 0, vel1.y);

        } else {
            // If the object is approaching from the top or bottom

            if (dY < 0) {
                // If the object is approaching from positive Y
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).add(0, (size1.y + size2.y) / 2));
            } else {
                // If the object is approaching from negative Y
                transform.setPosition(entity1, transform.getPosition(entity2, new Vector2f()).sub(0, (size1.y + size2.y) / 2));
            }

            // Velocity component
            velocity.setVelocity(entity1, vel1.mul(1, -collider.getRestitution(entity1)));

            if (Math.abs(vel1.y) < STICKY_THRESHOLD)
                velocity.setVelocity(entity1, vel1.x, 0);
        }
    }
}
