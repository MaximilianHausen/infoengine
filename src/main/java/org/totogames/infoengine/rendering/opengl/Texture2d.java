package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.totogames.infoengine.rendering.opengl.enums.*;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46C.*;

// TODO: Rework texture abstractions + Add Mipmaps
public class Texture2d implements IOglObject {
    private int id = -1;

    public Texture2d(ByteBuffer pixels, TextureInternalFormats internalFormat, TextureTexelFormats texelFormat, TextureDataTypes dataType, int width, int height) {
        init(pixels, internalFormat, texelFormat, dataType, width, height);
    }
    public Texture2d(@Nullable ByteBuffer pixels, @NotNull TexturePresets preset, int width, int height) {
        switch (preset) {
            case Color -> init(pixels, TextureInternalFormats.RGBA, TextureTexelFormats.RGBA, TextureDataTypes.UNSIGNED_BYTE, width, height);
            case DephthStencil -> init(pixels, TextureInternalFormats.DEPTH24_STENCIL8, TextureTexelFormats.DEPTH_STENCIL, TextureDataTypes.UNSIGNED_INT_24_8, width, height);
        }
    }

    private void init(@Nullable ByteBuffer pixels, @NotNull TextureInternalFormats internalFormat, @NotNull TextureTexelFormats texelFormat, @NotNull TextureDataTypes dataType, int width, int height) {
        if (id != -1) {
            Logger.log(LogSeverity.Debug, "TextureLoader", "Texture  " + id + " was already initialized");
            return;
        }

        id = glGenTextures();
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.getValue(), width, height, 0, texelFormat.getValue(), dataType.getValue(), pixels);
        Logger.log(LogSeverity.Debug, "TextureLoader", "Texture created in slot " + id);
    }

    public int getId() {
        return id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, getId());
    }

    public void dispose() {
        if (id != -1) {
            glDeleteTextures(id);
            Logger.log(LogSeverity.Debug, "TextureLoader", "Texture deleted from slot " + id);
            id = -1;

        } else
            Logger.log(LogSeverity.Debug, "TextureLoader", "Texture is already deleted");
    }

    // TODO: GetPixels from changeable texture attributes
    /*public ByteBuffer getPixels() {
        ByteBuffer temp = ByteBuffer.allocate(4 * getWidth() * getHeight());
        glGetTexImage(id, 0, GL_RGBA, GL_UNSIGNED_BYTE, temp);
        return temp;
    }*/

    public void reinitialize(@Nullable ByteBuffer pixels, @NotNull TexturePresets preset, int width, int height) {
        switch (preset) {
            case Color -> reinitialize(pixels, TextureInternalFormats.RGBA, TextureTexelFormats.RGBA, TextureDataTypes.UNSIGNED_BYTE, width, height);
            case DephthStencil -> reinitialize(pixels, TextureInternalFormats.DEPTH24_STENCIL8, TextureTexelFormats.DEPTH_STENCIL, TextureDataTypes.UNSIGNED_INT_24_8, width, height);
        }
    }
    public void reinitialize(@Nullable ByteBuffer pixels, @NotNull TextureInternalFormats internalFormat, @NotNull TextureTexelFormats texelFormat, @NotNull TextureDataTypes dataType, int width, int height) {
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.getValue(), width, height, 0, texelFormat.getValue(), dataType.getValue(), pixels);
        Logger.log(LogSeverity.Debug, "TextureLoader", "Texture " + id + " reinitialized");
    }

    public int getWidth() {
        bind();
        return glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
    }
    public int getHeight() {
        bind();
        return glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
    }

    public void setWrappingStyle(@NotNull TextureWrappingStyles style) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, style.getValue());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, style.getValue());
    }
    public void setResizeFilter(@NotNull TextureResizeFilters filter) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter.getValue());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter.getValue());
    }
}
