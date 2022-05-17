package net.totodev.infoengine.core.components;

import net.totodev.infoengine.ecs.IGlobalComponent;

public class UpdateRate implements IGlobalComponent {
    public int updateRate = 60;

    public String serializeState() {
        return Integer.toString(updateRate);
    }
    public void deserializeState(String data) {
        updateRate = Integer.parseInt(data);
    }
}
