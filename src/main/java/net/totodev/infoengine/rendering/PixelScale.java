package net.totodev.infoengine.rendering;

import net.totodev.infoengine.ecs.*;

public class PixelScale implements GlobalComponent {
    private int pixelsPerUnit;

    public int getPixelsPerUnit() {
        return pixelsPerUnit;
    }
    public void setPixelsPerUnit(int pixelsPerUnit) {
        this.pixelsPerUnit = pixelsPerUnit;
    }

    @Override
    public String serializeState() {
        return Integer.toString(pixelsPerUnit);
    }
    @Override
    public void deserializeState(String data) {
        pixelsPerUnit = Integer.parseInt(data);
    }
}
