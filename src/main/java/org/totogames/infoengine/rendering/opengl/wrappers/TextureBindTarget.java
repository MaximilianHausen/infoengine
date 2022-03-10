package org.totogames.infoengine.rendering.opengl.wrappers;

import org.jetbrains.annotations.NotNull;
import org.totogames.infoengine.rendering.opengl.enums.TextureType;
import org.totogames.infoengine.rendering.opengl.enums.TextureUnit;

/**
 * All information needed to specify a texture bind target
 */
public record TextureBindTarget(@NotNull TextureUnit texUnit, @NotNull TextureType texType) {
}
