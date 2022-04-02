package net.totodev.infoengine.loading;

public record SceneModel(String formatVersion, String name, int entityCount, String[] systems, ComponentModel[] components) {
    public SceneModel {
        systems = new String[0];
        components = new ComponentModel[0];
    }
}
