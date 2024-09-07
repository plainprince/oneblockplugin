package de.linkum.simeon.oneblockplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getLogger().info("OneBlock Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OneBlock Plugin disabled!");
    }
}