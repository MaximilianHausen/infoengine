package net.totodev.infoengine.ecs;

public interface ISystem {
    void initialize(Scene scene);
    void deinitialize(Scene scene);
}
