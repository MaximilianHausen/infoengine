package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.opengl.enums.ShaderParameter;
import net.totodev.infoengine.rendering.opengl.enums.ShaderType;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static org.lwjgl.opengl.GL46C.*;

/**
 * Java wrapper for OpenGL shader objects
 * @see <a href="https://www.khronos.org/opengl/wiki/GLSL_Object#Shader_objects">OpenGL Wiki: Shaders</a>
 */
public class Shader implements IOglObject {
    private final static HashMap<ShaderType, String> typeNames;

    static {
        // Initialize type names
        HashMap<ShaderType, String> temp = new HashMap<>();
        temp.put(ShaderType.VERTEX_SHADER, "VertexShader");
        temp.put(ShaderType.FRAGMENT_SHADER, "FragmentShader");
        temp.put(ShaderType.GEOMETRY_SHADER, "GeometryShader");
        temp.put(ShaderType.TESS_CONTROL_SHADER, "TessControlShader");
        temp.put(ShaderType.TESS_EVALUATION_SHADER, "TessEvaluationShader");
        typeNames = temp;
    }

    private final int id;
    private final String source;
    private final ShaderType shaderType;
    private boolean isDisposed = false;

    /**
     * Creates and compiles a new shader. This object will be disposed immediately if compilation failed.
     * @param source The source for the new shader
     * @param type   The type of shader
     */
    public Shader(@NotNull String source, @NotNull ShaderType type) {
        this.source = source;
        this.shaderType = type;

        id = glCreateShader(type.getValue());
        Logger.log(LogLevel.Debug, "OpenGL", typeNames.get(type) + " created in slot " + id);
        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.log(LogLevel.Critical, "OpenGL", typeNames.get(type) + " " + id + " could not be compiled");
            dispose();
            return;
        }

        Logger.log(LogLevel.Debug, "OpenGL", typeNames.get(type) + " compiled in slot " + id);
    }

    public @NotNull ShaderType getShaderType() {
        return shaderType;
    }

    public @NotNull String getSource() {
        if (isDisposed) throw new ShaderDisposedException();
        return source;
    }

    /**
     * Gets a shader parameter from the OpenGL object.
     * @param param The parameter to get
     * @return The value of the parameter as a OpenGL constant
     */
    public int getShaderParameter(@NotNull ShaderParameter param) {
        if (isDisposed) throw new ShaderDisposedException();
        return glGetShaderi(id, param.getValue());
    }

    public int getId() {
        if (isDisposed) throw new ShaderDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new ShaderDisposedException();
        glDeleteShader(id);
        isDisposed = true;
        Logger.log(LogLevel.Debug, "OpenGL", typeNames.get(shaderType) + " deleted from slot " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
