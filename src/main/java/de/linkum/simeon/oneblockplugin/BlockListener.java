package de.linkum.simeon.oneblockplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.Material.*;

public class BlockListener implements Listener {
    private final Main plugin;
    private final Location oneBlockLocation;
    private int stage = 1;
    private int highestStage = 1;
    private int blocksBroken = 0;
    private final Random random = new Random();
    private FriendSystem friendSystem;
    private String playerName;
    private int blocksBrokenAllTime = 0;

    public BlockListener(Main plugin, String worldName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(playerName);
                assert player != null;

                if(player.getWorld().getName().startsWith("customdimension")) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);

        this.plugin = plugin;
        World world = Bukkit.getWorld(worldName);
        this.oneBlockLocation = new Location(world, 0, 100, 0); // Setze hier die Koordinaten des OneBlock
        this.spawnNextBlock(oneBlockLocation.getBlock());
        this.playerName = worldName.substring(15);
        this.friendSystem = new FriendSystem(plugin, playerName);

        Command command = new Command("stage") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player && args.length == 1) {
                    if(!args[0].matches("-?\\d+")) {
                        sender.sendMessage(args[0] + " ist keine Zahl, bitte wähle eine stage größer als 0.");
                        return true;
                    }
                    if(!(Integer.parseInt(args[0]) > 0)) {
                        sender.sendMessage("Bitte wähle eine stage größer als 0.");
                        return true;
                    }
                    if(Integer.parseInt(args[0]) <= highestStage) {
                        stage = Integer.parseInt(args[0]);
                        blocksBroken = 0;
                        sender.sendMessage("Hat die stage zu: " + stage + " gesetzt");
                    } else {
                        sender.sendMessage("Du musst zuerst noch Blöcke abbauen!");
                    }
                }
                return true;
            }
        };

        this.plugin.getServer().getCommandMap().register("oneblock", command);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!block.getWorld().getName().equals(oneBlockLocation.getWorld().getName())) {
            return;
        }

        Bukkit.getLogger().info(String.valueOf(friendSystem.getFriends()));

        if(!player.getName().equals(playerName) && (friendSystem.getFriends() == null || !friendSystem.getFriends().contains(player.getName()))) {
            event.setCancelled(true);
            player.sendMessage("Hey! You can't break that here!");
            return;
        }

        if(block.getLocation().getX() != oneBlockLocation.getX() ||
                block.getLocation().getY() != oneBlockLocation.getY() ||
                block.getLocation().getZ() != oneBlockLocation.getZ()) {
            return;
        }

        blocksBroken++;
        blocksBrokenAllTime++;
        player.giveExp(event.getExpToDrop());
        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());

        for (ItemStack drop : drops) {
            Map<Integer, ItemStack> itemsThatDidNotFit = player.getInventory().addItem(drop);
            if (!itemsThatDidNotFit.isEmpty()) {
                for (ItemStack item : itemsThatDidNotFit.values()) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        Bukkit.getLogger().info("gamemode is creative");
                        break;
                    }
                    // Drop the item on the ground
                    player.getWorld().dropItemNaturally(oneBlockLocation, item);
                }
            }
        }

        event.setDropItems(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.spawnNextBlock(block), 1L);
        if(blocksBroken > (400 * stage) - 1) {
            blocksBroken = 0;
            stage++;
            if(stage > highestStage) {
                highestStage++;
                player.sendTitle("Neue Stage: " + highestStage, null, 20, 60, 20);
            }
        }
    }

    private void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("BlitzGamer", "dummy", ChatColor.GOLD + "BlitzGamer.javnet.de");
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        String dateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());

        Score textScore = objective.getScore(ChatColor.GREEN + "Der beste Server auf der Welt!");
        textScore.setScore(7);

        Score dateScore = objective.getScore(ChatColor.WHITE + "Datum & Uhrzeit: " + dateTime);
        dateScore.setScore(6);

        Score nameScore = objective.getScore(ChatColor.WHITE + "Dein Name: " + player.getName());
        nameScore.setScore(5);

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        Score onlineScore = objective.getScore(ChatColor.WHITE + "Online Spieler: " + onlinePlayers);
        onlineScore.setScore(4);

        Score stageScore = objective.getScore(ChatColor.WHITE + "Stage: " + stage);
        stageScore.setScore(3);

        Score highestStageScore = objective.getScore(ChatColor.WHITE + "Höchste stage: " + highestStage);
        highestStageScore.setScore(2);

        Score blocksBrokenScore = objective.getScore(ChatColor.WHITE + "OneBlocks zerstört: " + blocksBrokenAllTime);
        blocksBrokenScore.setScore(1);

        int currentStageBlocks = 400 * stage;
        Score blocksTillNextStage = objective.getScore(ChatColor.WHITE + "Blöcke bis zur nächsten stage: " + (currentStageBlocks - blocksBroken));
        blocksTillNextStage.setScore(0);

        player.setScoreboard(board);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!player.getWorld().getName().equals(oneBlockLocation.getWorld().getName())) {
            return;
        }

        if(!player.getName().equals(playerName) && (friendSystem.getFriends() == null || !friendSystem.getFriends().contains(player.getName()))) {
            event.setCancelled(true);
            if(event.getClickedBlock() == null) {
                return;
            }
            player.sendMessage(ChatColor.RED + "Hey! Du kannst das hier nicht machen!");
        }
    }

    public void spawnNextBlock(Block block) {
        switch (stage) {
            case 1:
                block.setType(getSurfaceBlock());
                break;
            case 2:
                block.setType(getUndergroundBlock());
                break;
            case 3:
                block.setType(getNetherBlock());
                break;
            case 4:
                block.setType(END_PORTAL_FRAME);
                break;
            default:
                block.setType(AIR);
                break;
        }
    }

        private Material getSurfaceBlock() {
            Material[] surfaceBlocks = {GRASS_BLOCK, OAK_LOG, BIRCH_LOG};
            return surfaceBlocks[random.nextInt(surfaceBlocks.length)];
        }

        private Material getUndergroundBlock() {
            Material[] undergroundBlocks = {Material.STONE, Material.IRON_ORE, GOLD_ORE, DIAMOND_ORE, LAPIS_ORE};
            return undergroundBlocks[random.nextInt(undergroundBlocks.length)];
        }

        private Material getNetherBlock() {
            Material[] netherBlocks = {Material.NETHERRACK, Material.NETHER_BRICKS, Material.NETHER_QUARTZ_ORE};
            return netherBlocks[random.nextInt(netherBlocks.length)];
        }
}