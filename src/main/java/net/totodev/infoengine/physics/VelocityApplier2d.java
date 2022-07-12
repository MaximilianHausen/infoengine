package net.totodev.infoengine.physics;

import net.totodev.infoengine.core.CoreEvents;
import net.totodev.infoengine.core.components.Transform2d;
import net.totodev.infoengine.ecs.*;
import org.joml.Vector2f;

public class VelocityApplier2d extends BaseSystem {
    @CachedComponent
    private Transform2d transform;
    @CachedComponent
    private Velocity2d velocity;

    @EventSubscriber(CoreEvents.Update)
    public void update(float deltaTime) {
        getScene().getEntitiesByComponents(Velocity2d.class, Transform2d.class).forEach(e -> {
            transform.move(e, velocity.getVelocity(e, new Vector2f()).div(deltaTime));
            transform.rotate(e, velocity.getRotVelocity(e) / deltaTime);
        });
    }
}
