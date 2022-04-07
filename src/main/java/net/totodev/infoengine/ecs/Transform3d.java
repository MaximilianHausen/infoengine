package net.totodev.infoengine.ecs;

import net.totodev.infoengine.loading.ComponentDataModel;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform3d implements IComponent {
    private final MutableIntObjectMap<Matrix4f> transforms = IntObjectMaps.mutable.empty();

    public Vector3f getPosition(int entityId, @NotNull Vector3f out) {
        if (!isPresentOn(entityId)) return null;
        return transforms.get(entityId).getTranslation(out);
    }
    public void setPosition(int entityId, float xPos, float yPos, float zPos) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).setTranslation(xPos, yPos, zPos);
    }
    public void setPosition(int entityId, @NotNull Vector3f pos) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).setTranslation(pos);
    }
    public void move(int entityId, float xOffset, float yOffset, float zOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translate(xOffset, yOffset, zOffset);
    }
    public void move(int entityId, @NotNull Vector3f posOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translate(posOffset);
    }

    public Quaternionf getRotation(int entityId, @NotNull Quaternionf out) {
        if (!isPresentOn(entityId)) return null;
        return transforms.get(entityId).getUnnormalizedRotation(out);
    }
    public void setRotation(int entityId, float angleX, float angleY, float angleZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                new Quaternionf().rotateXYZ(angleX, angleY, angleZ),
                getScale(entityId, new Vector3f()));
    }
    public void setRotation(int entityId, @NotNull Quaternionf rot) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                rot,
                getScale(entityId, new Vector3f()));
    }
    public void rotate(int entityId, float angleOffsetX, float angleOffsetY, float angleOffsetZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).rotateXYZ(angleOffsetX, angleOffsetY, angleOffsetZ);
    }
    public void rotate(int entityId, @NotNull Quaternionf rotOffset) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).rotate(rotOffset);
    }

    public Vector3f getScale(int entityId, @NotNull Vector3f out) {
        if (!isPresentOn(entityId)) return null;
        return transforms.get(entityId).getScale(out);
    }
    public void setScale(int entityId, float scaleX, float scaleY, float scaleZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                getRotation(entityId, new Quaternionf()),
                new Vector3f(scaleX, scaleY, scaleZ));
    }
    public void setScale(int entityId, @NotNull Vector3f scale) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).translationRotateScale(
                getPosition(entityId, new Vector3f()),
                getRotation(entityId, new Quaternionf()),
                scale);
    }
    // Multiplier, not offset
    public void scale(int entityId, float scaleX, float scaleY, float scaleZ) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).scale(scaleX, scaleY, scaleZ);
    }
    public void scale(int entityId, @NotNull Vector3f scale) {
        if (!isPresentOn(entityId)) return;
        transforms.get(entityId).scale(scale);
    }

    //region IComponent
    public void addOnEntity(int entityId) {
        transforms.put(entityId, new Matrix4f());
    }
    public void removeFromEntity(int entityId) {
        transforms.remove(entityId);
    }

    public void deserializeState(@NotNull ComponentDataModel data) {
        String[] serializedNumbers = data.data.split("\\|");
        float[] numbers = new float[16];
        for (int i = 0; i < 16; i++)
            numbers[i] = Float.parseFloat(serializedNumbers[i]);

        if (!isPresentOn(data.entity))
            addOnEntity(data.entity);

        transforms.get(data.entity).set(numbers);
    }

    public @Nullable String serializeState(int entityId) {
        if (!isPresentOn(entityId)) return null;

        float[] numbers = transforms.get(entityId).get(new float[16]);
        String[] serializedNumbers = new String[16];
        for (int i = 0; i < 16; i++)
            serializedNumbers[i] = Float.toString(numbers[i]);

        return String.join("|", serializedNumbers);
    }

    public boolean isPresentOn(int entityId) {
        return transforms.containsKey(entityId);
    }
    //endregion
}
