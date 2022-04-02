package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.Image;
import net.totodev.infoengine.rendering.opengl.enums.TextureDataType;
import net.totodev.infoengine.rendering.opengl.enums.TextureFormat;
import net.totodev.infoengine.rendering.opengl.enums.TextureInternalFormat;
import net.totodev.infoengine.rendering.opengl.enums.TextureType;
import net.totodev.infoengine.util.logging.LogLevel;
import net.totodev.infoengine.util.logging.Logger;

import java.nio.*;

import static org.lwjgl.opengl.GL46C.*;

// TODO: Mipmaps

/**
 * Java wrapper for OpenGL texture objects of type TEXTURE_2D
 * @see <a href="https://khronos.org/opengl/wiki/Texture">OpenGL Wiki: Textures</a>
 * @see <a href="https://www.khronos.org/opengl/wiki/Texture_Storage">OpenGL Wiki: Texture Storage</a>
 */
public class Texture2d extends Texture {
    /**
     * This only creates the texture. To initialize it, use setMutable or setImmutable
     */
    public Texture2d() {
        super(TextureType.TEXTURE_2D);
    }

    public static Texture2d fromImage(Image img) {
        Texture2d tex = new Texture2d();

        TextureInternalFormat internalFormat = switch (img.channels()) {
            case 1 -> TextureInternalFormat.RED;
            case 2 -> TextureInternalFormat.RG;
            case 3 -> TextureInternalFormat.RGB;
            default -> TextureInternalFormat.RGBA;
        };
        TextureFormat format = switch (img.channels()) {
            case 1 -> TextureFormat.RED;
            case 2 -> TextureFormat.RG;
            case 3 -> TextureFormat.RGB;
            default -> TextureFormat.RGBA;
        };

        tex.bind(0);
        tex.setMutable(0, internalFormat, img.width(), img.height());
        tex.setPartialData(0, 0, 0, img.width(), img.height(), format, TextureDataType.UNSIGNED_BYTE, img.pixels());
        tex.unbind();
        return tex;
    }

    /**
     * Initializes a single texture level as mutable
     * @param texLevel       The texture level to initialize
     * @param internalFormat The format of the texture
     * @param width          The width of the texture
     * @param height         The height of the texture
     */
    @RequiresBind
    public void setMutable(int texLevel, TextureInternalFormat internalFormat, int width, int height) {
        if (isDisposed()) throw new TextureDisposedException();
        // This works because the base formats from TextureInternalFormat are shared with TextureFormat and the specific format is not important because no data is transferred
        int format = internalFormat.getBaseFormat().getValue();
        glTexImage2D(GL_TEXTURE_2D, texLevel, internalFormat.getValue(), width, height, 0, format, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    }

    /**
     * Initializes all texture levels as immutable
     * @param texLevelCount  The amount of texture levels to initialize
     * @param internalFormat The format of the texture
     * @param width          The width of the 0th texture level
     * @param height         The height of the texture level
     */
    @RequiresBind
    public void setImmutable(int texLevelCount, TextureInternalFormat internalFormat, int width, int height) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexStorage2D(GL_TEXTURE_2D, texLevelCount, internalFormat.getValue(), width, height);
        Logger.log(LogLevel.Trace, "OpenGL", "Immutable storage allocated for texture " + getId() + " of type TEXTURE_2D");
    }

    //region SetPartialData
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, ByteBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, ShortBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, IntBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    // No LongBuffer version
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, FloatBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, DoubleBuffer data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, short[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, int[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, float[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    @RequiresBind
    public void setPartialData(int texLevel, int xOffset, int yOffset, int width, int height, TextureFormat format, TextureDataType dataType, double[] data) {
        if (isDisposed()) throw new TextureDisposedException();
        glTexSubImage2D(GL_TEXTURE_2D, texLevel, xOffset, yOffset, width, height, format.getValue(), dataType.getValue(), data);
        Logger.log(LogLevel.Trace, "OpenGL", "Partial data set for texture " + getId() + " of type TEXTURE_2D");
    }
    //endregion
}
