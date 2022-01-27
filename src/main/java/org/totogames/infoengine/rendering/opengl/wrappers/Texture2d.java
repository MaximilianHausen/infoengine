package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.totogames.infoengine.DisposedException;
import org.totogames.infoengine.rendering.opengl.enums.*;
import org.totogames.infoengine.rendering.opengl.enums.custom.TexturePresets;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46C.*;

// TODO: General texture (not only 2d) + Add Mipmaps
// This moves away from the original objects a bit more than the other wrappers
public class Texture2d implements IOglObject {
    private final int id;
    private boolean isDisposed = false;

    public Texture2d(@Nullable ByteBuffer pixels, @NotNull TextureInternalFormats internalFormat, @NotNull TextureTexelFormats texelFormat, @NotNull TextureDataTypes dataType, int width, int height) {
        id = glGenTextures();
        Logger.log(LogSeverity.Debug, "Texture", "Texture2d created with id " + id);
        bind();
        init(pixels, internalFormat, texelFormat, dataType, width, height);
        unbind();
    }
    public Texture2d(@Nullable ByteBuffer pixels, @NotNull TexturePresets preset, int width, int height) {
        // Code duplication because this() not possible in switch
        id = glGenTextures();
        Logger.log(LogSeverity.Debug, "Texture", "Texture2d created with id " + id);
        bind();
        switch (preset) {
            case Color -> init(pixels, TextureInternalFormats.RGBA, TextureTexelFormats.RGBA, TextureDataTypes.UNSIGNED_BYTE, width, height);
            case DephthStencil -> init(pixels, TextureInternalFormats.DEPTH24_STENCIL8, TextureTexelFormats.DEPTH_STENCIL, TextureDataTypes.UNSIGNED_INT_24_8, width, height);
        }
        unbind();
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @RequiresBind
    private void init(@Nullable ByteBuffer pixels, @NotNull TextureInternalFormats internalFormat, @NotNull TextureTexelFormats texelFormat, @NotNull TextureDataTypes dataType, int width, int height) {
        setResizeFilter(TextureResizeFilters.Nearest);
        setWrappingStyle(TextureWrappingStyles.Repeat);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.getValue(), width, height, 0, texelFormat.getValue(), dataType.getValue(), pixels);
        Logger.log(LogSeverity.Debug, "Texture", "Texture2d " + id + " initialized");
    }

    public void bind() {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        glBindTexture(GL_TEXTURE_2D, getId());
    }

    @RequiresBind
    public void setImage2d(@Nullable ByteBuffer pixels, @NotNull TexturePresets preset, int width, int height) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        switch (preset) {
            case Color -> setImage2d(pixels, TextureInternalFormats.RGBA, TextureTexelFormats.RGBA, TextureDataTypes.UNSIGNED_BYTE, width, height);
            case DephthStencil -> setImage2d(pixels, TextureInternalFormats.DEPTH24_STENCIL8, TextureTexelFormats.DEPTH_STENCIL, TextureDataTypes.UNSIGNED_INT_24_8, width, height);
        }
    }
    @RequiresBind
    public void setImage2d(@Nullable ByteBuffer pixels, @NotNull TextureInternalFormats internalFormat, @NotNull TextureTexelFormats texelFormat, @NotNull TextureDataTypes dataType, int width, int height) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.getValue(), width, height, 0, texelFormat.getValue(), dataType.getValue(), pixels);
        Logger.log(LogSeverity.Debug, "Texture", "Texture2d " + id + " reinitialized");
    }

    // TODO: GetPixels from changeable texture attributes
    /*public ByteBuffer getPixels() {
        ByteBuffer temp = ByteBuffer.allocate(4 * getWidth() * getHeight());
        glGetTexImage(id, 0, GL_RGBA, GL_UNSIGNED_BYTE, temp);
        return temp;
    }*/

    @RequiresBind
    public int getTexParameter(@NotNull TextureParameters param) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        return glGetTexParameteri(GL_TEXTURE_2D, param.getValue());
    }

    @RequiresBind
    public int getTexLevelParameter(int level, @NotNull TextureLevelParameters param) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        return glGetTexLevelParameteri(GL_TEXTURE_2D, level, param.getValue());
    }
    @RequiresBind
    public int getWidth(int level) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        return glGetTexLevelParameteri(GL_TEXTURE_2D, level, GL_TEXTURE_WIDTH);
    }
    @RequiresBind
    public int getHeight(int level) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        return glGetTexLevelParameteri(GL_TEXTURE_2D, level, GL_TEXTURE_HEIGHT);
    }

    @RequiresBind
    public void setWrappingStyle(@NotNull TextureWrappingStyles style) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, style.getValue());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, style.getValue());
    }
    @RequiresBind
    public void setResizeFilter(@NotNull TextureResizeFilters filter) {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter.getValue());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter.getValue());
    }

    public int getId() {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        return id;
    }

    public void dispose() {
        if (isDisposed) throw new DisposedException("Texture2d was already disposed");
        glDeleteTextures(id);
        isDisposed = true;
        Logger.log(LogSeverity.Debug, "Texture", "Texture2d deleted with id " + id);
    }
    public boolean isDisposed() {
        return isDisposed;
    }

    // TODO: General parameter set
}
