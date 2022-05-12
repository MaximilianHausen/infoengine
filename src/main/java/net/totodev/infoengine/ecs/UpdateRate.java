package net.totodev.infoengine.ecs;

public class UpdateRate implements IGlobalComponent {
    public int updateRate = 60;

    public String serializeState() {
        return Integer.toString(updateRate);
    }
    public void deserializeState(String data) {
        updateRate = Integer.parseInt(data);
    }
}
