package net.totodev.engine.util;

import static org.lwjgl.vulkan.VK10.VK_MAKE_API_VERSION;

public record SemVer(int major, int minor, int patch) {
    public int asVkVersion() {
        return VK_MAKE_API_VERSION(0, major, minor, patch);
    }
}
