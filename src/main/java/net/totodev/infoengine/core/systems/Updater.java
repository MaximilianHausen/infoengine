package net.totodev.infoengine.core.systems;

import net.totodev.infoengine.core.CoreEvents;
import net.totodev.infoengine.core.components.UpdateRate;
import net.totodev.infoengine.ecs.*;

public class Updater extends BaseSystem {
    private long lastFrameNanos;

    private Thread currentLoop;

    public void start(Scene scene) {
        super.start(scene);
        currentLoop = new Thread(() -> updateLoop(scene));
        currentLoop.start();
    }

    private void updateLoop(Scene scene) {
        while (!scene.isRunning()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //TODO: Proper update scheduling
        while (!Thread.interrupted()) {
            long currentNanos = java.lang.System.nanoTime();
            UpdateRate rateComponent = scene.getGlobalComponent(UpdateRate.class);
            if (currentNanos > lastFrameNanos + (1000000000 / (rateComponent == null ? 60 : rateComponent.updateRate))) {
                scene.events.invokeEvent(CoreEvents.Update);
                lastFrameNanos = currentNanos;
            }
        }
    }

    public void stop(Scene scene) {
        super.stop(scene);
        currentLoop.interrupt();
    }
}
