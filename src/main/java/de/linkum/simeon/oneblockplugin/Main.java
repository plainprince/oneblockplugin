package de.linkum.simeon.oneblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        createListenerInDimension("world");
        createListenerInDimension("world_nether");
        createListenerInDimension("world_the_end");
        createListenerInDimension("customdimension");
        getLogger().info("OneBlock Plugin enabled!");
    }

    public void createListenerInDimension(String dimension) {
        if (Bukkit.getWorld(dimension) == null) {
            World newWorld = Bukkit.createWorld(new WorldCreator(dimension));
            newWorld.setSpawnLocation(0, 101, 0);
        }

        Bukkit.getLogger().info(String.valueOf(Bukkit.getWorld(dimension)));

        getServer().getPluginManager().registerEvents(new BlockListener(this, dimension), this);
    }



    @Override
    public void onDisable() {
        getLogger().info("OneBlock Plugin disabled!");
    }
}