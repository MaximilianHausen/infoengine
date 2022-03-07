package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.rendering.opengl.enums.TextureDataType;
import org.totogames.infoengine.rendering.opengl.enums.TextureFormat;
import org.totogames.infoengine.rendering.opengl.enums.TextureInternalFormat;
import org.totogames.infoengine.rendering.opengl.enums.TextureType;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

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
