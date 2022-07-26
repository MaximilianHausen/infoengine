package net.totodev.engine.physics;

import net.totodev.engine.core.components.Transform2d;
import net.totodev.engine.core.systems.Updater;
import net.totodev.engine.ecs.*;
import org.joml.Vector2f;

public class VelocityApplier2d extends BaseSystem {
    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private Velocity2d velocity;

    @EventSubscriber(Updater.EVENT_POST_UPDATE)
    public void update(float deltaTime) {
        getScene().getEntitiesByComponents(Velocity2d.class, Transform2d.class).forEach(e -> {
            transform.move(e, velocity.getVelocity(e, new Vector2f()).mul(deltaTime));
            transform.rotate(e, velocity.getRotVelocity(e) * deltaTime);
        });
    }
}
