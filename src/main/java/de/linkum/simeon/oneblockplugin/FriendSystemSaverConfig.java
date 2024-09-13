package de.linkum.simeon.oneblockplugin;

import java.util.HashMap;
import java.util.Map;

public class FriendSystemSaverConfig {
    public Map<String, FriendSystem> data;

    public FriendSystemSaverConfig() {
        this.data = new HashMap<>();
    }

    public <T> void addData(String key, T value) {
        this.data.put(key, (FriendSystem) value);
    }

    public boolean hasData(String key) {
        return this.data.containsKey(key);
    }
}