package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

public class VertexShader extends Shader {
    public VertexShader(@NotNull String source) {
        super(source);
        compile();
    }

    protected void compile() {
        if (id != -1) {
            Logger.log(LogSeverity.Debug, "Shader", "VertexShader " + id + " is already compiled");
            return;
        }

        id = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.log(LogSeverity.Critical, "Shader", "VertexShader " + id + " could not be compiled");
            dispose();
            return;
        }

        Logger.log(LogSeverity.Debug, "Shader", "VertexShader compiled in slot " + id);
    }
}
