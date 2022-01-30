package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.totogames.infoengine.rendering.opengl.enums.*;
import org.totogames.infoengine.rendering.opengl.enums.texparams.TextureLevelParameter;
import org.totogames.infoengine.rendering.opengl.enums.texparams.TextureParameter;
import org.totogames.infoengine.rendering.opengl.enums.texparams.TextureResizeFilter;
import org.totogames.infoengine.rendering.opengl.enums.texparams.TextureWrappingStyle;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.*;

/**
 * @see <a href="https://khronos.org/opengl/wiki/Texture">OpenGL Wiki: Textures</a>
 * @see <a href="https://www.khronos.org/opengl/wiki/Texture_Storage">OpenGL Wiki: Texture Storage</a>
 */
public abstract class Texture implements IOglObject {
    private final int id;
    private final TextureType type;
    private boolean isDisposed = false;

    public Texture(@NotNull TextureType type) {
        id = glGenTextures();
        this.type = type;
        Logger.log(LogSeverity.Debug, "OpenGL", "Texture of type created with id " + id + " and type " + type);
    }

    public void bind(@Range(from = 0, to = 31) int textureUnit) {
        if (isDisposed) throw new TextureDisposedException();
        TextureUnit texUnit = TextureUnit.fromNumber(0); //TODO: Multiple texture units
        glActiveTexture(texUnit.getValue());
        glBindTexture(type.getValue(), id);
        Logger.log(LogSeverity.Trace, "OpenGL", "Texture " + id + " of type " + type + "bound to target " + texUnit);
    }
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new TextureDisposedException();
        glActiveTexture(TextureUnit.TEXTURE0.getValue());
        glBindTexture(type.getValue(), id);
        Logger.log(LogSeverity.Trace, "OpenGL", "Texture " + id + " of type " + type + " unbound from target GL_TEXTURE0");
    }

    @RequiresBind
    public int getTexParameter(@NotNull TextureParameter param) {
        if (isDisposed) throw new TextureDisposedException();
        return glGetTexParameteri(type.getValue(), param.getValue());
    }

    @RequiresBind
    public int getTexLevelParameter(int level, @NotNull TextureLevelParameter param) {
        if (isDisposed) throw new TextureDisposedException();
        return glGetTexLevelParameteri(type.getValue(), level, param.getValue());
    }
    @RequiresBind
    public int getWidth(int level) {
        if (isDisposed) throw new TextureDisposedException();
        return glGetTexLevelParameteri(type.getValue(), level, GL_TEXTURE_WIDTH);
    }
    @RequiresBind
    public int getHeight(int level) {
        if (isDisposed) throw new TextureDisposedException();
        return glGetTexLevelParameteri(type.getValue(), level, GL_TEXTURE_HEIGHT);
    }

    @RequiresBind
    public void setTexParam(@NotNull TextureParameter param, int value) {
        if (isDisposed) throw new TextureDisposedException();
    }

    @RequiresBind
    public void setWrappingStyle(@NotNull TextureWrappingStyle style) {
        if (isDisposed) throw new TextureDisposedException();
        glTexParameteri(type.getValue(), GL_TEXTURE_WRAP_S, style.getValue());
        glTexParameteri(type.getValue(), GL_TEXTURE_WRAP_T, style.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Wrapping style set to " + style + " on texture " + id + " of type " + type);
    }
    @RequiresBind
    public void setResizeFilter(@NotNull TextureResizeFilter filter) {
        if (isDisposed) throw new TextureDisposedException();
        glTexParameteri(type.getValue(), GL_TEXTURE_MIN_FILTER, filter.getValue());
        glTexParameteri(type.getValue(), GL_TEXTURE_MAG_FILTER, filter.getValue());
        Logger.log(LogSeverity.Trace, "OpenGL", "Texture resize filter set to " + filter + " on texture " + id + " of type " + type);
    }

    public int getId() {
        if (isDisposed) throw new TextureDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new TextureDisposedException();
        glDeleteTextures(id);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "OpenGL", "Texture of type deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
