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
    public int stage = 1;
    public int highestStage = 1;
    public int blocksBroken = 0;
    private final Random random = new Random();
    private FriendSystem friendSystem;
    private String playerName;
    private int blocksBrokenAllTime = 0;

    public BlockListener(Main plugin, String worldName, FriendSystem friendSystem) {
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
        this.friendSystem = friendSystem;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!block.getWorld().getName().equals(oneBlockLocation.getWorld().getName())) {
            return;
        }

        if(!player.getName().equals(playerName) && (friendSystem.getFriends() == null || !friendSystem.getFriends().contains(player.getName()))) {
            event.setCancelled(true);
            player.sendMessage("Hey! Du kannst das hier nicht zerstören!");
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
                    // Drop the item on the ground
                    player.getWorld().dropItemNaturally(oneBlockLocation, item);
                }
            }
        }

        event.setDropItems(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.spawnNextBlock(block), 1L);
        if(blocksBroken > (400 * stage) - 1 && stage < 14) {
            blocksBroken = 0;
            stage++;
            if(stage > highestStage) {
                highestStage++;
                player.sendTitle("Neue Stage: " + highestStage, null, 20, 60, 20);
            }
        }
    }

    private void updateScoreboard(@NotNull Player player) {
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
                block.setType(getWoodBlock(), false);
                break;
            case 2:
                block.setType(getStoneBlock(), false);
                break;
            case 3:
                block.setType(getPlainsBlock(), false);
                break;
            case 4:
                block.setType(getUndergroundBlock(), false);
                break;
            case 5:
                block.setType(getWinterBlock(), false);
                break;
            case 6:
                block.setType(getOceanBlock(), false);
                break;
            case 7:
                block.setType(getJungleBlock(), false);
                break;
            case 8:
                block.setType(getSwampBlock(), false);
                break;
            case 9:
                block.setType(getDungeonBlock(), false);
                break;
            case 10:
                block.setType(getDesertBlock(), false);
                break;
            case 11:
                block.setType(getNetherBlock(), false);
                break;
            case 12:
                block.setType(getPlentyBlock(), false);
                break;
            case 13:
                block.setType(getDesolationBlock(), false);
                break;
            default:
                block.setType(getEndBlock(), false);
                break;
        }
        block.getState().update(true, false);
    }

    private Material getWoodBlock() {
        Material[] woodBlocks = {
                ACACIA_LOG,
                BIRCH_LOG,
                DARK_OAK_LOG,
                JUNGLE_LOG,
                MANGROVE_LOG,
                OAK_LOG,
                SPRUCE_LOG,
        };

        return woodBlocks[random.nextInt(woodBlocks.length)];
    }

    private Material getStoneBlock() {
        Material[] stoneBlocks = {
                STONE,
                IRON_ORE,
                COAL_ORE,
                DEEPSLATE
        };

        return stoneBlocks[random.nextInt(stoneBlocks.length)];
    }

    private Material getPlainsBlock() {
        Material[] plainsBlocks = {
                PODZOL,
                MYCELIUM,
                BIRCH_LEAVES,
                IRON_ORE,
                BEE_NEST,
                OAK_LOG,
                DIRT,
                ANDESITE,
                INFESTED_STONE,
                DARK_OAK_LOG,
                OAK_LEAVES,
                BROWN_MUSHROOM_BLOCK,
                DIORITE,
                COAL_ORE,
                GRAVEL,
                DIAMOND_ORE,
                GOLD_ORE,
                GRASS_BLOCK,
                DARK_OAK_LEAVES,
                BIRCH_LOG,
                COBBLESTONE,
                SAND,
                GRANITE,
                COARSE_DIRT,
                STONE,
                CLAY,
                EMERALD_ORE
        };
        return plainsBlocks[random.nextInt(plainsBlocks.length)];
    }

    private Material getUndergroundBlock() {
        Material[] undergroundBlocks = {
                COAL_ORE,
                GRAVEL,
                REDSTONE_ORE,
                DIAMOND_ORE,
                GOLD_ORE,
                IRON_ORE,
                COBBLESTONE,
                SAND,
                GRANITE,
                COBWEB,
                DIRT,
                STONE,
                OBSIDIAN,
                ANDESITE,
                SANDSTONE,
                EMERALD_ORE,
                LAPIS_ORE,
                DIORITE
        };
        return undergroundBlocks[random.nextInt(undergroundBlocks.length)];
    }

    private Material getWinterBlock() {
        Material[] winterBlocks = {
                COBBLESTONE,
                SAND,
                DIRT,
                STONE,
                SPRUCE_LEAVES,
                STRIPPED_SPRUCE_LOG,
                ICE,
                GOLD_ORE,
                LAPIS_ORE,
                SPRUCE_LOG,
                SNOW_BLOCK
        };
        return winterBlocks[random.nextInt(winterBlocks.length)];
    }

    private Material getOceanBlock() {
        Material[] oceanBlocks = {
                BRAIN_CORAL_BLOCK,
                BUBBLE_CORAL_BLOCK,
                FIRE_CORAL_BLOCK,
                TUBE_CORAL_BLOCK,
                HORN_CORAL_BLOCK,
                DIRT,
                ANDESITE,
                SANDSTONE,
                RED_SAND,
                WET_SPONGE,
                GRAVEL,
                PRISMARINE,
                TURTLE_EGG,
                SAND,
                GRANITE,
                STONE,
                CLAY,
                SPONGE,
                SEA_LANTERN,
                DARK_PRISMARINE
        };

        return oceanBlocks[random.nextInt(oceanBlocks.length)];
    }

    private Material getJungleBlock() {
        Material[] jungleBlocks = {
                PODZOL,
                COAL_ORE,
                MYCELIUM,
                GRAVEL,
                DIAMOND_ORE,
                PUMPKIN,
                GOLD_ORE,
                GRASS_BLOCK,
                JUNGLE_LEAVES,
                JUNGLE_LOG,
                COBBLESTONE,
                COARSE_DIRT,
                STRIPPED_JUNGLE_LOG,
                DIRT,
                STONE,
                MELON,
                EMERALD_ORE,
                LAPIS_ORE
        };

        return jungleBlocks[random.nextInt(jungleBlocks.length)];
    }

    private Material getSwampBlock() {
        Material[] swampBlocks = {
                COAL_ORE,
                GRAVEL,
                DIAMOND_ORE,
                GOLD_ORE,
                GRASS_BLOCK,
                COBBLESTONE,
                COARSE_DIRT,
                OAK_LOG,
                DIRT,
                STONE,
                OAK_LEAVES,
                CLAY,
                EMERALD_ORE,
                LAPIS_ORE
        };

        return swampBlocks[random.nextInt(swampBlocks.length)];
    }

    private Material getDungeonBlock() {
        Material[] dungeonBlocks = {
                COAL_ORE,
                GRAVEL,
                REDSTONE_ORE,
                DIAMOND_ORE,
                INFESTED_COBBLESTONE,
                GOLD_ORE,
                IRON_ORE,
                COBBLESTONE,
                OAK_PLANKS,
                SAND,
                GRANITE,
                DIRT,
                STONE,
                ANDESITE,
                INFESTED_STONE,
                SANDSTONE,
                EMERALD_BLOCK,
                RED_MUSHROOM_BLOCK,
                BROWN_MUSHROOM_BLOCK,
                LAPIS_ORE,
                DIORITE,
                MOSSY_COBBLESTONE
        };

        return dungeonBlocks[random.nextInt(dungeonBlocks.length)];
    }

    private Material getDesertBlock() {
        Material[] desertBlocks = {
                SAND,
                STONE,
                RED_SANDSTONE,
                SANDSTONE,
                RED_SAND
        };

        return desertBlocks[random.nextInt(desertBlocks.length)];
    }

    private Material getNetherBlock() {
        Material[] netherBlocks = {
                GRAVEL,
                NETHER_QUARTZ_ORE,
                NETHERRACK,
                SOUL_SAND,
                RED_NETHER_BRICKS,
                NETHER_BRICKS,
                GLOWSTONE,
                MAGMA_BLOCK
        };

        return netherBlocks[random.nextInt(netherBlocks.length)];
    }

    private Material getPlentyBlock() {
        Material[] plentyBlocks = {
                PODZOL,
                REDSTONE_ORE,
                HONEYCOMB_BLOCK,
                TERRACOTTA,
                IRON_ORE,
                ANDESITE,
                SPRUCE_LOG,
                JUNGLE_LOG,
                OAK_LOG,
                DARK_OAK_LOG,
                LAPIS_ORE,
                COAL_ORE,
                DIAMOND_ORE,
                PUMPKIN,
                GOLD_ORE,
                GRASS_BLOCK,
                ACACIA_LOG,
                BIRCH_LOG,
                JACK_O_LANTERN,
                GRANITE,
                HAY_BLOCK,
                STONE,
                MELON,
                DIORITE,
                EMERALD_ORE,
                GRASS_BLOCK
        };

        return plentyBlocks[random.nextInt(plentyBlocks.length)];
    }

    private Material getDesolationBlock() {
        Material[] desolationBlocks = {
                PODZOL,
                REDSTONE_ORE,
                HONEYCOMB_BLOCK,
                TERRACOTTA,
                IRON_ORE,
                OAK_LOG,
                DARK_OAK_LOG,
                LAPIS_ORE,
                COAL_ORE,
                DIAMOND_ORE,
                PUMPKIN,
                GOLD_ORE,
                GRASS_BLOCK,
                ACACIA_LOG,
                BIRCH_LOG,
                JACK_O_LANTERN,
                GRANITE,
                HAY_BLOCK,
                STONE,
                DIORITE,
                MELON,
                EMERALD_ORE
        };

        return desolationBlocks[random.nextInt(desolationBlocks.length)];
    }

    private Material getEndBlock() {
        Material[] endBlocks = {
                PURPUR_BLOCK,
                END_STONE_BRICKS,
                PURPLE_WOOL,
                OBSIDIAN,
                END_STONE,
                CHEST,
                TERRACOTTA,
                PURPUR_PILLAR
        };

        return endBlocks[random.nextInt(endBlocks.length)];
    }
}