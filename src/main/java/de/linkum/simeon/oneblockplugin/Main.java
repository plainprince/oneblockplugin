package de.linkum.simeon.oneblockplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {
    public Plugin worldguard = getServer().getPluginManager().getPlugin("worldguard");

    @Override
    public void onEnable() {
        getLogger().info(String.valueOf(worldguard));
        Bukkit.getWorlds().forEach(i -> {
            getLogger().info("World: " + i.getName());
            getLogger().info("OneBlockWorld?: " + i.getName().startsWith("customdimension"));
            if(i.getName().startsWith("customdimension")) {
                createListenerInDimension(i.getName());
            }
            getLogger().info("");
        });
        getLogger().info("OneBlock Plugin enabled!");

        // Register the custom command with Bukkit
        Command command = new Command("create") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 0) {
                    String dimensionName = "customdimension" + player.getName();
                    if(Bukkit.getWorld(dimensionName) != null) {
                        player.sendMessage("you already have a OneBlock, to go to your OneBlock run /oneblock tp");
                    }else {
                        player.sendMessage("building oneBlock");
                        getLogger().info("building oneBlock");
                        createListenerInDimension(dimensionName);
                        player.sendMessage("done building oneBlock");
                        Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                        player.sendMessage("teleporting...");
                        player.teleport(tpLocation);
                    }
                } else {
                    sender.sendMessage("Incorrect usage of command. use /oneblock:[create, tp, clear, addFriend, removeFriend, stage]");
                }
                return true; // Indicates that the command has been handled by this executor.
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);

        command = new Command("tp") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 0) {
                    String dimensionName = "customdimension" + player.getName();
                    if (Bukkit.getWorld(dimensionName) != null) {
                        Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                        player.sendMessage("teleporting...");
                        player.teleport(tpLocation);
                    } else {
                        player.sendMessage("you don't have a OneBlock yet, run /oneblock:create first");
                    }
                }

                if (sender instanceof Player player && args.length == 1) {
                    String dimensionName = "customdimension" + args[0];
                    if (Bukkit.getWorld(dimensionName) != null) {
                        Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                        player.sendMessage("teleporting...");
                        player.teleport(tpLocation);
                    } else {
                        player.sendMessage("this oneblock does not exist");
                    }
                }
                return true;
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);

        command = new Command("clear") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 0) {
                    player.sendMessage("This will delete your entire Island. If you really want to do this run /oneblock:forceClear");
                }
                return true;
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);

        command = new Command("forceClear") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 0) {
                    String dimensionName = "customdimension" + player.getName();
                    player.sendMessage("starting to clear island");
                    for (int i = -32; i < 32; i++) {
                        for (int j = -64; j < 320; j++) {
                            for (int k = -32; k < 32; k++) {
                                Block block = Bukkit.getWorld(dimensionName).getBlockAt(i, j, k);
                                block.setType(Material.AIR);
                            }
                        }
                    }
                    Bukkit.getWorld(dimensionName).getBlockAt(0, 100, 0).setType(Material.OAK_LOG);
                    player.sendMessage("cleared island");
                }
                return true;
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);

        new FriendSystem(this, "thisplayerdoesnotexist");
    }

    public void createListenerInDimension(String dimension) {
        if (Bukkit.getWorld(dimension) == null) {
            WorldCreator worldCreator = new WorldCreator(dimension);
            worldCreator.type(WorldType.FLAT);
            worldCreator.generateStructures(false);
            World newWorld = Bukkit.createWorld(worldCreator);
            newWorld.setSpawnLocation(0, 101, 0);
            newWorld.getWorldBorder().setCenter(0.5, 0.5);
            newWorld.getWorldBorder().setSize(65);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            for (int i = -32; i < 33; i++) {
                for (int j = -64; j < -60; j++) {
                    for (int k = -32; k < 33; k++) {
                        Block block = Bukkit.getWorld(dimension).getBlockAt(i, j, k);
                        block.setType(Material.AIR);
                    }
                }
            }

            Block block = Bukkit.getWorld(dimension).getBlockAt(0, 100, 0);
            block.setType(Material.OAK_LOG);
        }

        getServer().getPluginManager().registerEvents(new BlockListener(this, dimension), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("OneBlock Plugin disabled!");
    }
}