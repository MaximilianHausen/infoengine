package org.totogames.infoengine.loading;

public class EntityModel {
    public String name;
    public int x;
    public int y;
    public int z;
    // TODO: Rotation

    public EntityModel[] children = new EntityModel[0];
    public ComponentModel[] components = new ComponentModel[0];
}
