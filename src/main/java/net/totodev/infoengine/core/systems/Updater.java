package net.totodev.infoengine.core.systems;

import net.totodev.infoengine.core.CoreEvents;
import net.totodev.infoengine.core.components.UpdateRate;
import net.totodev.infoengine.ecs.*;

public class Updater implements ISystem {
    private long lastFrameNanos;

    private Thread currentLoop;

    public void start(Scene scene) {
        currentLoop = new Thread(() -> updateLoop(scene));
        currentLoop.start();
    }

    private void updateLoop(Scene scene) {
        //TODO: Proper update scheduling
        while (!Thread.interrupted()) {
            long currentNanos = System.nanoTime();
            UpdateRate rateComponent = scene.getGlobalComponent(UpdateRate.class);
            if (currentNanos > lastFrameNanos + (1000000000 / (rateComponent == null ? 60 : rateComponent.updateRate))) {
                scene.events.invokeEvent(CoreEvents.Update.getName());
                lastFrameNanos = currentNanos;
            }
        }
    }

    public void stop(Scene scene) {
        currentLoop.interrupt();
    }
}
