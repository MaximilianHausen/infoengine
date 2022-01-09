package org.totogames.infoframework.loading;

import java.util.Map;

public class EntityModel {
    public String name;
    public String type;
    public int x;
    public int y;
    public int z;
    // TODO: Rotation

    public Map<String, Object> data;

    public EntityModel[] children;
}
