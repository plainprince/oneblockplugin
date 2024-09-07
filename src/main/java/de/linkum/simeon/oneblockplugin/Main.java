package de.linkum.simeon.oneblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    public void regenerateBlock(BlockListener listener) {
        new BukkitRunnable() {
            @Override
            public void run() {
                listener.spawnNextBlock();
            }
        }.runTaskTimer(this, 0L, 100L); // 100L = 5 Sekunden (20 Ticks pro Sekunde)
    }
}