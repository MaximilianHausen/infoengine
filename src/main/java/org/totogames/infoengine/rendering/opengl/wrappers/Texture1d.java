package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.rendering.opengl.enums.TextureDataType;
import org.totogames.infoengine.rendering.opengl.enums.TextureFormat;
import org.totogames.infoengine.rendering.opengl.enums.TextureInternalFormat;
import org.totogames.infoengine.rendering.opengl.enums.TextureType;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

public class Texture1d extends Texture {
    public Texture1d() {
        super(TextureType.TEXTURE_1D);
    }

    @RequiresBind
    public void setImmutable(int mipmapLevelCount, TextureInternalFormat format, int width) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexStorage1D(GL_TEXTURE_1D, mipmapLevelCount, format.getValue(), width);
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable storage allocated for texture " + getId() + " of type TEXTURE_1D");
    }

    //region SetPartialData
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, long data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, ByteBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, ShortBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, IntBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    // No LongBuffer version
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, FloatBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, DoubleBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, short[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, int[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, float[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int offset, int width, TextureFormat format, TextureDataType dataType, double[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage1D(GL_TEXTURE_1D, mipmapLevel, offset, width, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_1D");
    }
    //endregion
}
