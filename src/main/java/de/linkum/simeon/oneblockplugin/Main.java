package de.linkum.simeon.oneblockplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        createListenerInDimension("world");
        createListenerInDimension("world_nether");
        createListenerInDimension("world_the_end");
        getLogger().info("OneBlock Plugin enabled!");
    }

    public void createListenerInDimension(String dimension) {
        getServer().getPluginManager().registerEvents(new BlockListener(this, dimension), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("OneBlock Plugin disabled!");
    }
}