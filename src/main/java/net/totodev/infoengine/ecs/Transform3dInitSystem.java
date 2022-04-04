package net.totodev.infoengine.ecs;

import org.jetbrains.annotations.NotNull;

//TODO: Unsubscribe events
public class Transform3dInitSystem implements ISystem {
    public void initialize(@NotNull Scene scene) {
        Transform3d transform = scene.getComponent(Transform3d.class);
        scene.getAllEntities().forEach(transform::addOnEntity);

        scene.events.subscribe(CoreEvents.CreateEntity.toString(), Integer.class, (Integer e) -> scene.getComponent(Transform3d.class).addOnEntity(e));
        scene.events.subscribe(CoreEvents.DestroyEntity.toString(), Integer.class, (Integer e) -> scene.getComponent(Transform3d.class).removeFromEntity(e));
    }
}
