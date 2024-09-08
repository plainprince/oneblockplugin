package de.linkum.simeon.oneblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getWorlds().forEach(i -> createListenerInDimension(i.getName()));
        getLogger().info("OneBlock Plugin enabled!");

        // Register the custom command with Bukkit
        Command command = new Command("oneblock") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 0) {
                    // Perform your custom command logic here
                    // For example: send a message to the chat
                    String dimensionName = "customdimension" + player.getName();
                    if(Bukkit.getWorld(dimensionName) != null) {
                        player.sendMessage("you already have a OneBlock");
                    }else {
                        player.sendMessage("building oneBlock");
                        Bukkit.getLogger().info("building oneBlock");
                        createListenerInDimension(dimensionName);
                        player.sendMessage("done building oneBlock");
                    }
                    Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                    player.sendMessage("teleporting...");
                    player.teleport(tpLocation);
                } else {
                    Bukkit.getLogger().warning("Incorrect usage of the custom command.");
                }
                return true; // Indicates that the command has been handled by this executor.
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);
    }

    public void createListenerInDimension(String dimension) {
        if (Bukkit.getWorld(dimension) == null) {
            World newWorld = Bukkit.createWorld(new WorldCreator(dimension));
            newWorld.setSpawnLocation(0, 101, 0);
            newWorld.getWorldBorder().setCenter(0, 0);
            newWorld.getWorldBorder().setSize(64);
        }

        getServer().getPluginManager().registerEvents(new BlockListener(this, dimension), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("OneBlock Plugin disabled!");
    }
}