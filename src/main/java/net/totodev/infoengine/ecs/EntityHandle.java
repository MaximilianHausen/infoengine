package net.totodev.infoengine.ecs;

/**
 * Represents an entity in a specific scene and provides a bunch of utility methods as a less performant but more convenient alternative to using entity ids directly.
 * No guarantees are made for the represented entity actually existing in the scene.
 * @param id    The entity id
 * @param scene The scene the entity belongs to
 */
public record EntityHandle(int id, Scene scene) {
}
