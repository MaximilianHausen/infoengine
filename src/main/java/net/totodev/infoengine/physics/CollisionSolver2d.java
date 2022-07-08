package net.totodev.infoengine.physics;

import net.totodev.infoengine.core.components.Transform2d;
import net.totodev.infoengine.ecs.*;
import org.joml.Vector2f;

public class CollisionSolver2d extends BaseSystem {
    private static final float STICKY_THRESHOLD = 0.01f;

    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private BoxCollider2d collider;
    @CachedComponent
    private Velocity2d velocity;

    @EventSubscriber("Phys:default2d")
    public void solveCollision(int e1, int e2) {
        // Adapted from https://developer.ibm.com/tutorials/wa-build2dphysicsengine
        Vector2f pos1 = transform.getPosition(e1, new Vector2f()).add(collider.getOffset(e1));
        Vector2f pos2 = transform.getPosition(e2, new Vector2f()).add(collider.getOffset(e2));

        Vector2f size1 = collider.getSize(e1);
        Vector2f size2 = collider.getSize(e2);

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
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).add((size1.x + size2.x) / 2, 0));
            } else {
                // If the object is approaching from negative X
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).sub((size1.x + size2.x) / 2, 0));
            }

            if (dY < 0) {
                // If the object is approaching from positive Y
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).add(0, (size1.y + size2.y) / 2));
            } else {
                // If the object is approaching from negative Y
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).sub(0, (size1.y + size2.y) / 2));
            }

            // Randomly select a x/y direction to reflect velocity on
            if (Math.random() < .5) {

                // Reflect the velocity at a reduced rate
                velocity.setVelocity(e1, velocity.getVelocity(e1).mul(-collider.getRestitution(e1), 1, new Vector2f()));

                // If the object's velocity is nearing 0, set it to 0
                // STICKY_THRESHOLD is set to .0004
                if (velocity.getVelocity(e1).x < STICKY_THRESHOLD)
                    velocity.setVelocity(e1, 0, velocity.getVelocity(e1).y);
            } else {
                velocity.setVelocity(e1, velocity.getVelocity(e1).mul(1, -collider.getRestitution(e1), new Vector2f()));

                if (velocity.getVelocity(e1).y < STICKY_THRESHOLD)
                    velocity.setVelocity(e1, velocity.getVelocity(e1).x, 0);
            }

        } else if (absDX > absDY) {
            // If the object is approaching from the sides

            if (dX < 0) {
                // If the object is approaching from positive X
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).add((size1.x + size2.x) / 2, 0));
            } else {
                // If the object is approaching from negative X
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).sub((size1.x + size2.x) / 2, 0));
            }

            // Velocity component
            velocity.setVelocity(e1, velocity.getVelocity(e1).mul(-collider.getRestitution(e1), 1, new Vector2f()));

            if (velocity.getVelocity(e1).x < STICKY_THRESHOLD)
                velocity.setVelocity(e1, 0, velocity.getVelocity(e1).y);

        } else {
            // If the object is approaching from the top or bottom

            if (dY < 0) {
                // If the object is approaching from positive Y
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).add(0, (size1.y + size2.y) / 2));
            } else {
                // If the object is approaching from negative Y
                transform.setPosition(e1, transform.getPosition(e2, new Vector2f()).sub(0, (size1.y + size2.y) / 2));
            }

            // Velocity component
            velocity.setVelocity(e1, velocity.getVelocity(e1).mul(1, -collider.getRestitution(e1), new Vector2f()));

            if (velocity.getVelocity(e1).y < STICKY_THRESHOLD)
                velocity.setVelocity(e1, velocity.getVelocity(e1).x, 0);
        }
    }

    private void solveX() {

    }
}
