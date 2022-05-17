package net.totodev.infoengine.ecs;

public interface IGlobalComponent {
    String serializeState();
    void deserializeState(String data);
}
