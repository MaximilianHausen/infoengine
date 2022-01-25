package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.DisposedException;
import org.totogames.infoengine.rendering.opengl.enums.ShaderTypes;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

public class ShaderProgram implements IOglObject {
    private static ShaderProgram defaultShader;

    private final int id;
    private boolean isDisposed = false;
    private Shader vertexShader;
    private Shader fragmentShader;

    // TODO: Any shader types with Shader... shaders
    public ShaderProgram(@NotNull Shader vertexShader, @NotNull Shader fragmentShader) {
        {
            String error = null;
            if (vertexShader.isDisposed() || fragmentShader.isDisposed())
                error = "Program could not be linked because some shaders are already disposed";
            if (vertexShader.getShaderType() != ShaderTypes.VERTEX_SHADER)
                error = "Program could not be linked because the shader specified as the vertex shader is not a vertex shader";
            if (fragmentShader.getShaderType() != ShaderTypes.FRAGMENT_SHADER)
                error = "Program could not be linked because the shader specified as the fragment shader is not a fragment shader";

            if (error != null) {
                Logger.log(LogSeverity.Error, "Shader", error);
                isDisposed = true;
                id = -1;
                return;
            }
        }

        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;

        id = glCreateProgram();
        glAttachShader(id, vertexShader.getId());
        glAttachShader(id, fragmentShader.getId());
        glLinkProgram(id);

        Logger.log(LogSeverity.Debug, "Shader", "Program linked with id " + id);
    }

    public static ShaderProgram getDefault() {
        if (defaultShader == null) {
            //region vertShaderSource
            String vertShaderSource =
                    """
                            #version 460 core
                            layout (location = 0) in vec3 vPos;
                            uniform mat4 model;
                            uniform mat4 view;
                            uniform mat4 projection;
                            void main()
                            {
                            	gl_Position = projection * view * model * vec4(vPos, 1.0);
                            }""";
            //endregion
            //region fragShaderSource
            String fragShaderSource =
                    """
                            #version 460 core
                            out vec4 FragColor;
                            void main()
                            {
                            	vec4 texColor = vec4(1.0, 1.0, 1.0, 1.0);
                            	FragColor = texColor;
                            }""";
            //endregion
            defaultShader = new ShaderProgram(new Shader(vertShaderSource, ShaderTypes.VERTEX_SHADER), new Shader(fragShaderSource, ShaderTypes.FRAGMENT_SHADER));
        }
        return defaultShader;
    }


    public void use() {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        glUseProgram(getId());
    }

    public Shader getVertexShader() {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        return vertexShader;
    }
    public Shader getFragmentShader() {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        return fragmentShader;
    }

    public int getId() {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        glDeleteProgram(id);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "Shader", "Program deleted from slot " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
