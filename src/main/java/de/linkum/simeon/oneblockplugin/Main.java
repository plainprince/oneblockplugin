package de.linkum.simeon.oneblockplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {
    private FriendSystemSaverConfig config = new FriendSystemSaverConfig();
    private BlockListenerSaverConfig blockListenerConfig = new BlockListenerSaverConfig();
    @Override
    public void onEnable() {
        Bukkit.getWorlds().forEach(i -> {
            if(i.getName().startsWith("customdimension")) {
                createListenerInDimension(i.getName());
            }
        });
        getLogger().info("OneBlock Plugin enabled!");

        // Register the custom command with Bukkit
        Command command = new Command("create") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 0) {
                    String dimensionName = "customdimension" + player.getName();
                    if(Bukkit.getWorld(dimensionName) != null) {
                        player.sendMessage("Du hast schon einen oneblock, benutze /oneblock tp");
                    }else {
                        player.sendMessage("building oneBlock");
                        createListenerInDimension(dimensionName);
                        player.sendMessage("done building oneBlock");
                        Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                        player.sendMessage("teleporting...");
                        player.teleport(tpLocation);
                    }
                } else {
                    sender.sendMessage("Falsche benutzung des commands, benutze /oneblock:[create, tp, clear, addFriend, removeFriend, stage]");
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
                        player.sendMessage("Du hast noch keinen OneBlock, führe /oneblock:create zuerst aus");
                    }
                }

                if (sender instanceof Player player && args.length == 1) {
                    String dimensionName = "customdimension" + args[0];
                    if (Bukkit.getWorld(dimensionName) != null) {
                        Location tpLocation = new Location(Bukkit.getWorld(dimensionName), 0.5, 101, 0.5);
                        player.sendMessage("Teleportieren...");
                        player.teleport(tpLocation);
                    } else {
                        player.sendMessage("Dieser OneBlock exestiert nicht");
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
                    player.sendMessage("Das wird dein ganzen OneBlock säubern, wenn du das wirklich machen willst führ /oneblock:forceClear aus");
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
                    player.sendMessage("anfangen den OneBlock zu säubern");
                    for (int i = -32; i < 32; i++) {
                        for (int j = -64; j < 320; j++) {
                            for (int k = -32; k < 32; k++) {
                                Block block = Bukkit.getWorld(dimensionName).getBlockAt(i, j, k);
                                block.setType(Material.AIR);
                            }
                        }
                    }
                    Bukkit.getWorld(dimensionName).getBlockAt(0, 100, 0).setType(Material.OAK_LOG);
                    player.sendMessage("OneBlock gesäubert");
                }
                return true;
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);

        command = new Command("addFriend") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (!config.hasData(sender.getName())) {
                    sender.sendMessage("Du hast noch kein OneBlock, führ /oneblock:create zuerst aus!");
                    return true;
                }
                if (sender instanceof Player player && args.length == 1) {
                    FriendSystem friendSystem = config.data.get(sender.getName());
                    if(friendSystem.getFriends().contains(args[0])) {
                        player.sendMessage("Du hast schon diesen Freund hinzugefügt");
                        return true;
                    }
                    friendSystem.addFriend(args[0]);
                    player.sendMessage("Du hast einen Freund hinzugefügt: " + args[0]);
                }
                return true;
            }
        };

        getServer().getCommandMap().register("oneblock", command);

        command = new Command("removeFriend") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (!config.hasData(sender.getName())) {
                    sender.sendMessage("Du hast noch kein OneBlock, führ /oneblock:create zuerst aus!");
                    return true;
                }
                if (sender instanceof Player player && args.length == 1) {
                    config.data.get(sender.getName()).removeFriend(args[0]);
                    player.sendMessage("Du hast einen Freund entfernt: " + args[0]);
                }
                return true;
            }
        };

        getServer().getCommandMap().register("oneblock", command);

        command = new Command("getFriends") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (!config.hasData(sender.getName())) {
                    sender.sendMessage("Du hast noch kein OneBlock, führ /oneblock:create zuerst aus!");
                    return true;
                }
                if (sender instanceof Player player && args.length == 0) {
                    FriendSystem friendSystem = config.data.get(player.getName());
                    String[] friendsArray = friendSystem.getFriends().toArray(new String[0]);
                    String friends = Arrays.toString(friendsArray);
                    friends = friends.substring(1, friends.length() - 1);
                    if(friends.equals("")) {
                        player.sendMessage("Du hast noch keine Freunde");
                        return true;
                    }
                    player.sendMessage("Deine Freunde sind: " + friends);
                }
                return true;
            }
        };

        getServer().getCommandMap().register("oneblock", command);

        command = new Command("stage") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player && args.length == 1) {
                    if(!args[0].matches("-?\\d+")) {
                        sender.sendMessage(args[0] + " ist keine Zahl, bitte wähle eine stage größer als 0.");
                        return true;
                    }
                    if(Integer.parseInt(args[0]) <= 0) {
                        sender.sendMessage("Bitte wähle eine stage größer als 0.");
                        return true;
                    }
                    if(Integer.parseInt(args[0]) <= blockListenerConfig.data.get(sender.getName()).highestStage) {
                        blockListenerConfig.data.get(sender.getName()).stage = Integer.parseInt(args[0]);
                        blockListenerConfig.data.get(sender.getName()).blocksBroken = 0;
                        sender.sendMessage("Hat die stage zu: " + args[0] + " gesetzt");
                    } else {
                        sender.sendMessage("Du musst zuerst noch Blöcke abbauen!");
                    }
                }
                return true;
            }
        };

        this.getServer().getCommandMap().register("oneblock", command);
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

        FriendSystem friendSystem = new FriendSystem(dimension.substring(15));
        config.addData(dimension.substring(15), friendSystem);
        BlockListener blockListener = new BlockListener(this, dimension, friendSystem);
        blockListenerConfig.addData(dimension.substring(15), blockListener);
        getServer().getPluginManager().registerEvents(blockListener, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("OneBlock Plugin disabled!");
    }
}