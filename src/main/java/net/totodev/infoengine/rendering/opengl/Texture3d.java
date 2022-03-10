package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.opengl.enums.TextureDataType;
import net.totodev.infoengine.rendering.opengl.enums.TextureFormat;
import net.totodev.infoengine.rendering.opengl.enums.TextureInternalFormat;
import net.totodev.infoengine.rendering.opengl.enums.TextureType;
import net.totodev.infoengine.util.logging.LogSeverity;
import net.totodev.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

/**
 * Java wrapper for OpenGL texture objects of type TEXTURE_3D (Unfinished)
 * @see <a href="https://khronos.org/opengl/wiki/Texture">OpenGL Wiki: Textures</a>
 * @see <a href="https://www.khronos.org/opengl/wiki/Texture_Storage">OpenGL Wiki: Texture Storage</a>
 */
public class Texture3d extends Texture {
    public Texture3d() {
        super(TextureType.TEXTURE_3D);
    }

    @RequiresBind
    public void setImmutable(int mipmapLevelCount, TextureInternalFormat format, int width, int height, int depth) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexStorage3D(GL_TEXTURE_3D, mipmapLevelCount, format.getValue(), width, height, depth);
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable storage allocated for texture " + getId() + " of type TEXTURE_3D");
    }

    //region setPartialData
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, ByteBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, ShortBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, IntBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    // No LongBuffer version
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, FloatBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, DoubleBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, short[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, int[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, float[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int zOffset, int width, int height, int depth, TextureFormat format, TextureDataType dataType, double[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage3D(GL_TEXTURE_3D, mipmapLevel, xOffset, yOffset, zOffset, width, height, depth, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_3D");
    }
    //endregion
}
