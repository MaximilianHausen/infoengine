package net.totodev.infoengine.ecs;

public interface ISystem {
    default void added(Scene scene) {}
    void start(Scene scene);
    void stop(Scene scene);
    default void removed(Scene scene) {}
}
