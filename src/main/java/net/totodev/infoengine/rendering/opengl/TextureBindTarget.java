package net.totodev.infoengine.rendering.opengl;

import net.totodev.infoengine.rendering.opengl.enums.TextureType;
import net.totodev.infoengine.rendering.opengl.enums.TextureUnit;
import org.jetbrains.annotations.NotNull;

/**
 * All information needed to specify a texture bind target
 */
public record TextureBindTarget(@NotNull TextureUnit texUnit, @NotNull TextureType texType) {
}
