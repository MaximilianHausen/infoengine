package org.totogames.infoengine.rendering.opengl;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.util.logging.LogSeverity;
import org.totogames.infoengine.util.logging.Logger;

import static org.lwjgl.opengl.GL46C.glDeleteShader;

public abstract class Shader implements IOglObject {
    protected int id = -1;
    protected @NotNull String source;

    public Shader(@NotNull String source) {
        this.source = source;
    }

    protected abstract void compile();

    public @NotNull String getSource() {
        return source;
    }

    public boolean isCompiled() {
        return id != -1;
    }

    public int getId() {
        if (id == -1)
            compile();

        return id;
    }

    public void dispose() {
        String[] splitName = getClass().getName().split("\\.");

        if (id != -1) {
            int oldId = id;
            glDeleteShader(id);
            id = -1;
            Logger.log(LogSeverity.Debug, "Shader", splitName[splitName.length - 1] + " deleted from slot " + oldId);
        } else
            Logger.log(LogSeverity.Debug, "Shader", splitName[splitName.length - 1] + " is already deleted");
    }
}
