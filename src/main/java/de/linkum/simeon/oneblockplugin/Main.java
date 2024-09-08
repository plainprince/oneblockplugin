package de.linkum.simeon.oneblockplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getWorlds().forEach(i -> {
            Bukkit.getLogger().info("World: " + i.getName());
            Bukkit.getLogger().info("OneBlockWorld?: " + i.getName().startsWith("customdimension"));
            if(i.getName().startsWith("customdimension")) {
                createListenerInDimension(i.getName());
            }
        });
        getLogger().info("OneBlock Plugin enabled!");

        // Register the custom command with Bukkit
        Command command = new Command("oneblock") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length > 0) {
                    String dimensionName = "customdimension" + player.getName();
                    if(args[0].equals("create")) {
                        if(Bukkit.getWorld(dimensionName) != null) {
                            player.sendMessage("you already have a OneBlock, to go to your OneBlock run /oneblock tp");
                        }else {
                            player.sendMessage("building oneBlock");
                            Bukkit.getLogger().info("building oneBlock");
                            createListenerInDimension(dimensionName);
                            player.sendMessage("done building oneBlock");
                            Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                            player.sendMessage("teleporting...");
                            player.teleport(tpLocation);
                        }
                    }
                    if(args[0].equals("tp")) {
                        if(Bukkit.getWorld(dimensionName) != null) {
                            Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                            player.sendMessage("teleporting...");
                            player.teleport(tpLocation);
                        }else {
                            player.sendMessage("you don't have a OneBlock yet, run /oneblock create first");
                        }
                    }
                    if(args[0].equals("clear")) {
                        player.sendMessage("This will delete your entire Island. If you really want to do this run /oneblock forceClear");
                    }
                    if(args[0].equals("forceClear")) {
                        for (int i = -32; i < 32; i++) {
                            for (int j = -64; j < 320; j++) {
                                for (int k = -32; k < 32; k++) {
                                    Block block = Bukkit.getWorld(dimensionName).getBlockAt(i, j, k);
                                    if(block.getLocation().equals(new Location(Bukkit.getWorld(dimensionName), 0, 100, 0))) {
                                        break;
                                    }
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                    if(args[0].equals("addFriend")) {

                    }
                    if(args[0].equals("removeFriend")) {

                    }
                } else {
                    sender.sendMessage("Incorrect usage of command. use /oneblock [create, tp, clear, addFriend, removeFriend] or /oneblockstage <stage>");
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