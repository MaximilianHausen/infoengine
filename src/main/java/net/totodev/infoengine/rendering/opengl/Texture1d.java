package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.opengl.enums.TextureDataType;
import net.totodev.infoengine.rendering.opengl.enums.TextureFormat;
import net.totodev.infoengine.rendering.opengl.enums.TextureInternalFormat;
import net.totodev.infoengine.rendering.opengl.enums.TextureType;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

/**
 * Java wrapper for OpenGL texture objects of type TEXTURE_1D (Unfinished)
 * @see <a href="https://khronos.org/opengl/wiki/Texture">OpenGL Wiki: Textures</a>
 * @see <a href="https://www.khronos.org/opengl/wiki/Texture_Storage">OpenGL Wiki: Texture Storage</a>
 */
public class Texture1d extends Texture {
    public Texture1d() {
        super(TextureType.TEXTURE_1D);
    }

    @RequiresBind
    public void setImmutable(int mipmapLevelCount, TextureInternalFormat format, int width) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexStorage1D(GL_TEXTURE_1D, mipmapLevelCount, format.getValue(), width);
        Logger.log(LogLevel.Trace, "OpenGL", "Immutable storage allocated for texture " + getId() + " of type TEXTURE_1D");
    }

    //region SetPartialData
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, ByteBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, ShortBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, IntBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    // No LongBuffer version
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, FloatBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, DoubleBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, short[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, int[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, float[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, double[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    //endregion
}
