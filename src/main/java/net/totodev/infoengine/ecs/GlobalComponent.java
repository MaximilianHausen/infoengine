package net.totodev.infoengine.ecs;

public interface GlobalComponent {
    String serializeState();
    void deserializeState(String data);
}
