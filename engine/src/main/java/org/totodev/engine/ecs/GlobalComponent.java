package org.totodev.engine.ecs;

public interface GlobalComponent {
    String serializeState();
    void deserializeState(String data);
}
