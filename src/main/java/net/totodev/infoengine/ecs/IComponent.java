package net.totodev.infoengine.ecs;

public interface IComponent {
    Scene getScene();

    boolean isPresentOn(int entityId);

    boolean allowsMultiple();
}
