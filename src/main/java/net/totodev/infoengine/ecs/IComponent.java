package net.totodev.infoengine.ecs;

public interface IComponent {
    boolean isPresentOn(int entityId);
    boolean allowsMultiple();
}
