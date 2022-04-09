package net.totodev.infoengine.rendering;

import net.totodev.infoengine.core.Window;
import net.totodev.infoengine.ecs.Scene;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.lwjgl.opengl.GL46C.*;

/**
 * A renderer can render a scene to a render target
 */
public class Renderer {
    private Scene scene;
    private IRenderTarget target;
    private Thread activeThread;

    public Renderer(@NotNull Scene scene, @NotNull IRenderTarget target) {
        this.scene = scene;
        this.target = target;
    }

    /**
     * Starts rendering on a new thread.
     */
    public void startRender() {
        if (activeThread == null) {
            Window.makeNotCurrent();
            activeThread = new Thread(this::renderLoop);
            activeThread.start();
        } else Logger.log(LogLevel.Error, "Renderer", "Render thread already running");
    }

    /**
     * Stops rendering on the currently active render thread.
     */
    public void stopRender() {
        if (activeThread != null) {
            activeThread.interrupt();
            activeThread = null;
        } else Logger.log(LogLevel.Error, "Renderer", "No render thread running");
    }

    public @Nullable Thread getActiveThread() {
        return activeThread;
    }
    public void setScene(@NotNull Scene scene) {
        this.scene = scene;
    }
    public void setTarget(@NotNull IRenderTarget target) {
        this.target = target;
    }

    private void renderLoop() {
        Window.getActiveWindow().makeCurrent();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        Logger.log(LogLevel.Debug, "Renderer", "Render loop started on Thread " + Thread.currentThread().getName());

        int[] viewportSize = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewportSize);

        while (!Thread.interrupted()) {
            target.activate();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            /*scene.beforeRender();
            scene.render();
            scene.afterRender();*/

            target.renderedFrame();
        }
    }
}
