package org.totodev.engine.core.components;

import org.totodev.engine.ecs.GlobalComponent;

public class UpdateRate implements GlobalComponent {
    public int updateRate = 60;

    public String serializeState() {
        return Integer.toString(updateRate);
    }
    public void deserializeState(String data) {
        updateRate = Integer.parseInt(data);
    }
}
