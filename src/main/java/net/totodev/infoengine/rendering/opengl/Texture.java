package net.totodev.infoengine.rendering.opengl;

import com.google.common.collect.HashBiMap;
import net.totodev.infoengine.rendering.opengl.enums.TextureType;
import net.totodev.infoengine.rendering.opengl.enums.TextureUnit;
import net.totodev.infoengine.rendering.opengl.enums.texparams.TextureLevelParameter;
import net.totodev.infoengine.rendering.opengl.enums.texparams.TextureParameter;
import net.totodev.infoengine.rendering.opengl.enums.texparams.TextureResizeFilter;
import net.totodev.infoengine.rendering.opengl.enums.texparams.TextureWrappingStyle;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static org.lwjgl.opengl.GL46C.*;

/**
 * Base class for all texture wrappers. Contains all the general texture stuff.
 * @see <a href="https://khronos.org/opengl/wiki/Texture">OpenGL Wiki: Textures</a>
 * @see <a href="https://www.khronos.org/opengl/wiki/Texture_Storage">OpenGL Wiki: Texture Storage</a>
 */
public abstract class Texture implements IOglObject {
    private static final HashBiMap<Texture, TextureBindTarget> bindStatus = HashBiMap.create(32);
    private final int id;
    private final TextureType type;
    private boolean isDisposed = false;

    public Texture(@NotNull TextureType type) {
        id = glGenTextures();
        this.type = type;
        Logger.log(LogLevel.Debug, "OpenGL", "Texture created with id " + id + " and type " + type);
    }

    /**
     * Gets the currently bound texture
     * @param target The target to get the texture from
     * @return The texture, or null if nothing is bound on that target
     */
    public static @Nullable Texture getBoundTexture(@NotNull TextureBindTarget target) {
        return bindStatus.inverse().get(target);
    }

    /**
     * Activates the texture unit this is currently bound to
     */
    @RequiresBind
    public void activate() {
        if (isDisposed) throw new TextureDisposedException();
        if (bindStatus.containsKey(this))
            glActiveTexture(bindStatus.get(this).texUnit().getValue());
    }

    /**
     * Binds this texture and activates the respective texture unit
     * @param textureUnit The texture unit to bind to
     */
    public void bind(@Range(from = 0, to = 31) int textureUnit) {
        if (isDisposed) throw new TextureDisposedException();
        TextureUnit texUnit = TextureUnit.fromNumber(textureUnit);
        glActiveTexture(texUnit.getValue());
        glBindTexture(type.getValue(), id);
        bindStatus.forcePut(this, new TextureBindTarget(texUnit, type));
        Logger.log(LogLevel.Trace, "OpenGL", "Texture " + id + " of type " + type + " bound to target " + texUnit);
    }

    /**
     * Unbinds this texture from the texture unit it is currently bound to
     */
    @RequiresBind
    public void unbind() {
        if (isDisposed) throw new TextureDisposedException();
        TextureBindTarget target = bindStatus.get(this);
        if (target != null) {
            TextureUnit oldTexUnit = TextureUnit.fromNumber(glGetInteger(GL_ACTIVE_TEXTURE));
            glActiveTexture(target.texUnit().getValue());
            glBindTexture(target.texUnit().getValue(), 0);
            bindStatus.remove(this);
            glActiveTexture(oldTexUnit.getValue());
            Logger.log(LogLevel.Trace, "OpenGL", "Texture " + id + " of type " + type + " unbound from unit " + target.texUnit().toNumber());
        }
    }

    public @Nullable TextureBindTarget getBindStatus() {
        if (isDisposed) throw new TextureDisposedException();
        return bindStatus.get(this);
    }

    /**
     * Gets a texture parameter from the OpenGL object.
     * @param param The parameter to get
     * @return The value of the parameter as a OpenGL constant
     */
    @RequiresBind
    public int getTexParameter(@NotNull TextureParameter param) {
        if (isDisposed) throw new TextureDisposedException();
        return glGetTexParameteri(type.getValue(), param.getValue());
    }

    /**
     * Gets a texture level parameter from the OpenGL object.
     * @param level The Mipmap level to read from
     * @param param The parameter to get
     * @return The value of the parameter as a OpenGL constant
     */
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

    /**
     * Sets a texture parameter on the OpenGL object.
     * @param param The parameter to set
     * @param value The value of the parameter as a OpenGL constant
     */
    @RequiresBind
    public void setTexParam(@NotNull TextureParameter param, int value) {
        if (isDisposed) throw new TextureDisposedException();
        Logger.log(LogLevel.Trace, "OpenGL", "Parameter " + param + " for texture " + id + " of type " + type + " set to " + value);
    }

    @RequiresBind
    public void setWrappingStyle(@NotNull TextureWrappingStyle style) {
        if (isDisposed) throw new TextureDisposedException();
        glTexParameteri(type.getValue(), GL_TEXTURE_WRAP_S, style.getValue());
        glTexParameteri(type.getValue(), GL_TEXTURE_WRAP_T, style.getValue());
        Logger.log(LogLevel.Trace, "OpenGL", "Wrapping style set to " + style + " on texture " + id + " of type " + type);
    }
    @RequiresBind
    public void setResizeFilter(@NotNull TextureResizeFilter filter) {
        if (isDisposed) throw new TextureDisposedException();
        glTexParameteri(type.getValue(), GL_TEXTURE_MIN_FILTER, filter.getValue());
        glTexParameteri(type.getValue(), GL_TEXTURE_MAG_FILTER, filter.getValue());
        Logger.log(LogLevel.Trace, "OpenGL", "Texture resize filter set to " + filter + " on texture " + id + " of type " + type);
    }

    public int getId() {
        if (isDisposed) throw new TextureDisposedException();
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new TextureDisposedException();
        glDeleteTextures(id);
        isDisposed = true;
        Logger.log(LogLevel.Debug, "OpenGL", "Texture of type deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }
}
