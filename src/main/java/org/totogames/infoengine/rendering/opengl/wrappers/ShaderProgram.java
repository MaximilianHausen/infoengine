package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.totogames.infoengine.DisposedException;
import org.totogames.infoengine.rendering.opengl.enums.ShaderType;
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
            if (vertexShader.getShaderType() != ShaderType.VERTEX_SHADER)
                error = "Program could not be linked because the shader specified as the vertex shader is not a vertex shader";
            if (fragmentShader.getShaderType() != ShaderType.FRAGMENT_SHADER)
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
            String vertShaderSource =
                    """
                            #version 460 core
                            layout (location = 0) in vec3 vPos;
                            void main()
                            {
                            	gl_Position = vec4(vPos, 1.0);
                            }""";
            String fragShaderSource =
                    """
                            #version 460 core
                            out vec4 FragColor;
                            void main()
                            {
                            	vec4 texColor = vec4(1.0, 1.0, 1.0, 1.0);
                            	FragColor = texColor;
                            }""";
            defaultShader = new ShaderProgram(new Shader(vertShaderSource, ShaderType.VERTEX_SHADER), new Shader(fragShaderSource, ShaderType.FRAGMENT_SHADER));
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

    //region Uniforms
    public void setUniform(String name, int... values) {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        switch (values.length) {
            case 1 -> glUniform1iv(glGetUniformLocation(getId(), name), values);
            case 2 -> glUniform2iv(glGetUniformLocation(getId(), name), values);
            case 3 -> glUniform3iv(glGetUniformLocation(getId(), name), values);
            case 4 -> glUniform4iv(glGetUniformLocation(getId(), name), values);
        }
    }
    public void setUniform(String name, float... values) {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        switch (values.length) {
            case 1 -> glUniform1fv(glGetUniformLocation(getId(), name), values);
            case 2 -> glUniform2fv(glGetUniformLocation(getId(), name), values);
            case 3 -> glUniform3fv(glGetUniformLocation(getId(), name), values);
            case 4 -> glUniform4fv(glGetUniformLocation(getId(), name), values);
        }
    }
    public void setUniform(String name, double... values) {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        switch (values.length) {
            case 1 -> glUniform1dv(glGetUniformLocation(getId(), name), values);
            case 2 -> glUniform2dv(glGetUniformLocation(getId(), name), values);
            case 3 -> glUniform3dv(glGetUniformLocation(getId(), name), values);
            case 4 -> glUniform4dv(glGetUniformLocation(getId(), name), values);
        }
    }

    public void setUniform(String name, Matrix2f matrix) {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        glUniformMatrix2fv(glGetUniformLocation(getId(), name), false, matrix.get(new float[4]));
    }
    public void setUniform(String name, Matrix3f matrix) {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        glUniformMatrix3fv(glGetUniformLocation(getId(), name), false, matrix.get(new float[9]));
    }
    public void setUniform(String name, Matrix4f matrix) {
        if (isDisposed) throw new DisposedException("ShaderProgram was already disposed");
        glUniformMatrix4fv(glGetUniformLocation(getId(), name), false, matrix.get(new float[16]));
    }

    //TODO: Get Uniforms
    //endregion

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
