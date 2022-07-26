package net.totodev.engine.core.systems;

import net.totodev.engine.core.Engine;
import net.totodev.engine.core.components.UpdateRate;
import net.totodev.engine.ecs.*;
import org.lwjgl.glfw.GLFW;

public class Updater extends BaseSystem {
    public static final String EVENT_PRE_UPDATE = "PreUpdate";
    public static final String EVENT_UPDATE = "Update";
    public static final String EVENT_POST_UPDATE = "PostUpdate";

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
                scene.events.invokeEvent(EVENT_PRE_UPDATE, (currentNanos - lastFrameNanos) / 1000000000f);
                scene.events.invokeEvent(EVENT_UPDATE, (currentNanos - lastFrameNanos) / 1000000000f);
                scene.events.invokeEvent(EVENT_POST_UPDATE, (currentNanos - lastFrameNanos) / 1000000000f);
                lastFrameNanos = currentNanos;
            }
        }
    }

    public void stop(Scene scene) {
        super.stop(scene);
        currentLoop.interrupt();
    }
}
