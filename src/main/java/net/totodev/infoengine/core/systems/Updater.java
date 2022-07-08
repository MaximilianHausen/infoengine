package net.totodev.infoengine.core.systems;

import net.totodev.infoengine.core.*;
import net.totodev.infoengine.core.components.UpdateRate;
import net.totodev.infoengine.ecs.*;
import org.lwjgl.glfw.GLFW;

public class Updater extends BaseSystem {
    @CachedComponent
    private UpdateRate updateRate;

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
            if (currentNanos > lastFrameNanos + (1000000000 / (updateRate == null ? 60 : updateRate.updateRate))) {
                Engine.executeOnMainThread(GLFW::glfwPollEvents);
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
