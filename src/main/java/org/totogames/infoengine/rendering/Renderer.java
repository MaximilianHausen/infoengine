package org.totogames.infoengine.rendering;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.totogames.infoengine.ecs.Scene;
import org.totogames.infoengine.rendering.opengl.enums.BufferBindTarget;
import org.totogames.infoengine.rendering.opengl.enums.BufferUsage;
import org.totogames.infoengine.rendering.opengl.enums.VertexAttribDataType;
import org.totogames.infoengine.rendering.opengl.wrappers.Buffer;
import org.totogames.infoengine.rendering.opengl.wrappers.ShaderProgram;
import org.totogames.infoengine.rendering.opengl.wrappers.VertexArray;
import org.totogames.infoengine.rendering.opengl.wrappers.VertexAttribute;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

public class Renderer {
    private Scene scene;
    private IRenderTarget target;
    private Thread activeThread;

    public Renderer(@NotNull Scene scene, @NotNull IRenderTarget target) {
        this.scene = scene;
        this.target = target;
    }

    public void startRender() {
        if (activeThread == null) {
            Window.makeNotCurrent();
            activeThread = new Thread(this::renderLoop);
            activeThread.start();
        } else Logger.log(LogSeverity.Error, "Renderer", "Render thread already running");
    }
    public void stopRender() {
        if (activeThread != null) {
            activeThread.interrupt();
            activeThread = null;
        } else Logger.log(LogSeverity.Error, "Renderer", "No render thread running");
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

        Logger.log(LogSeverity.Debug, "Renderer", "Render loop started on Thread " + Thread.currentThread().getName());

        int[] viewportSize = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewportSize);

        float[] vertices = {
                0.5f, 0.5f, 0.0f,  // top right
                0.5f, -0.5f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f,  // bottom left
                -0.5f, 0.5f, 0.0f   // top left
        };
        int[] indices = {
                0, 1, 3,
                1, 2, 3
        };

        Buffer vbo = new Buffer();
        vbo.bind(BufferBindTarget.ARRAY_BUFFER);
        vbo.setData(vertices, BufferUsage.STATIC_DRAW);
        vbo.unbind();
        Buffer ebo = new Buffer();
        ebo.bind(BufferBindTarget.ELEMENT_ARRAY_BUFFER);
        ebo.setData(indices, BufferUsage.STATIC_DRAW);
        ebo.unbind();

        VertexArray vao = new VertexArray();
        vao.bind();
        vao.setElementBuffer(ebo);
        vao.setVertexAttributes(new VertexAttribute(vbo, VertexAttribDataType.FLOAT, 3));
        vao.unbind();

        while (!Thread.interrupted()) {
            target.activate();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            ShaderProgram.getDefault().use();
            vao.bind();
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            vao.unbind();

            scene.beforeRender();
            scene.render();
            scene.afterRender();

            target.renderedFrame();
        }
    }
}
