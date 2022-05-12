package net.totodev.infoengine.ecs;

public class Updater implements ISystem {
    private Scene scene;
    private long lastFrameNanos;

    private Thread currentLoop;

    public void initialize(Scene scene) {
        this.scene = scene;
    }
    public void start() {
        currentLoop = new Thread(this::updateLoop);
        currentLoop.start();
    }

    private void updateLoop() {
        //TODO: Proper update timings
        while (!Thread.interrupted()) {
            long currentNanos = System.nanoTime();
            UpdateRate rateComponent = scene.getGlobalComponent(UpdateRate.class);
            if (currentNanos > lastFrameNanos + (1000000000 / (rateComponent == null ? 60 : rateComponent.updateRate))) {
                scene.events.invokeEvent(CoreEvents.Update.getName());
                lastFrameNanos = currentNanos;
            }
        }
    }

    public void deinitialize(Scene scene) {
        currentLoop.interrupt();
    }
}
