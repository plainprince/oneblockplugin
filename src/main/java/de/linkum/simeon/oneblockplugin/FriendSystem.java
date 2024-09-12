package de.linkum.simeon.oneblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendSystem {
    private List<String> friends = new ArrayList<String>();
    private Main plugin;
    public FriendSystem(Main plugin, String playerName) {
        this.plugin = plugin;
        Bukkit.getLogger().info("created friendSystem for: " + playerName);

        Command command = new Command("addFriend") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                Bukkit.getLogger().info(playerName + " " + sender.getName());
                if (sender instanceof Player player && args.length == 1 && (getFriends().contains(sender.getName()) || playerName.equals(sender.getName()))) {
                    addFriend(args[0]);
                    Bukkit.getLogger().info("added friend " + args[0]);
                    player.sendMessage("added friend " + args[0]);
                }
                return true;
            }
        };

        plugin.getServer().getCommandMap().register("oneblock", command);

        command = new Command("removeFriend") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player player && args.length == 1 && playerName.equals(player.getName())) {
                    removeFriend(args[0]);
                    player.sendMessage("removed friend " + args[0]);
                }
                return true;
            }
        };

        plugin.getServer().getCommandMap().register("oneblock", command);
    }


    public void addFriend(String friendName) {
        this.friends.add(friendName);
    }

    public void removeFriend(String friendName) {
        this.friends.remove(friendName);
    }

    public List<String> getFriends() {
        return this.friends;
    }
}
