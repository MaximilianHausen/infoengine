package org.totogames.infoengine.rendering.opengl.wrappers;

import org.totogames.infoengine.rendering.Image;
import org.totogames.infoengine.rendering.opengl.enums.TextureDataType;
import org.totogames.infoengine.rendering.opengl.enums.TextureFormat;
import org.totogames.infoengine.rendering.opengl.enums.TextureInternalFormat;
import org.totogames.infoengine.rendering.opengl.enums.TextureType;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

public class Texture2d extends Texture {
    /**
     * This only creates the texture. To initialize it, use setMutable or setImmutable
     */
    public Texture2d() {
        super(TextureType.TEXTURE_2D);
    }

    public static Texture2d fromImage(Image img) {
        Texture2d tex = new Texture2d();

        TextureInternalFormat internalFormat = switch (img.getChannels()) {
            case 1 -> TextureInternalFormat.RED;
            case 2 -> TextureInternalFormat.RG;
            case 3 -> TextureInternalFormat.RGB;
            default -> TextureInternalFormat.RGBA;
        };
        TextureFormat format = switch (img.getChannels()) {
            case 1 -> TextureFormat.RED;
            case 2 -> TextureFormat.RG;
            case 3 -> TextureFormat.RGB;
            default -> TextureFormat.RGBA;
        };

        tex.setMutable(0, internalFormat, img.getWidth(), img.getHeight());
        tex.setPartialData(0, 0, 0, img.getWidth(), img.getHeight(), format, TextureDataType.UNSIGNED_BYTE, img.getPixels());
        return tex;
    }

    @RequiresBind
    public void setMutable(int mipmapLevel, TextureInternalFormat internalFormat, int width, int height) {
        if (isDisposed()) throw new TextureDisposedException();
        // This works because the base formats from TextureInternalFormat are shared with TextureFormat and the specific format is not important because no data is transferred
        int format = internalFormat.getBaseFormat().getValue();
        glTexImage2D(GL_TEXTURE_2D, mipmapLevel, internalFormat.getValue(), width, height, 0, format, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    }

    @RequiresBind
    public void setImmutable(int mipmapLevelCount, TextureInternalFormat format, int width, int height) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexStorage2D(GL_TEXTURE_2D, mipmapLevelCount, format.getValue(), width, height);
        Logger.log(LogSeverity.Trace, "OpenGL", "Immutable storage allocated for texture " + getId() + " of type TEXTURE_2D");
    }

    //region SetPartialData
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, long data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, ByteBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, ShortBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, IntBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    // No LongBuffer version
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, FloatBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, DoubleBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, short[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, int[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, float[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int mipmapLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, double[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, mipmapLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogSeverity.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    //endregion
}
