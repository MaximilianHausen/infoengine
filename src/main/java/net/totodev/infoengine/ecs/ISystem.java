package net.totodev.infoengine.ecs;

public interface ISystem {
    void initialize(Scene scene);
    void start();
    void deinitialize(Scene scene);
}
