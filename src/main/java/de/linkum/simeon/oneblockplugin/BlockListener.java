package de.linkum.simeon.oneblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.function.Supplier;

public class BlockListener implements Listener {

    private final Main plugin;
    private final Location oneBlockLocation;
    private int stage = 1;
    private int blocksBroken = 0;
    private final Random random = new Random();

    public BlockListener(Main plugin) {
        this.plugin = plugin;
        this.oneBlockLocation = new Location(Bukkit.getWorld("world"), 0, 100, 0); // Setze hier die Koordinaten des OneBlock
        plugin.regenerateBlock(this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getLocation().equals(oneBlockLocation)) {
            // event.setDropItems(false); // Verhindert, dass der Block normale Drops gibt
            blocksBroken++;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.spawnNextBlock();
                // Find the nearest item to the block and teleport it to the target location
                double closestDistance = Double.MAX_VALUE;
                Item closestItem = null;

                for (Entity entity : block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 10, 10, 10)) {
                    if (entity instanceof Item && !entity.isDead()) {
                        Bukkit.getLogger().info("entity found");
                        double distance = entity.getLocation().distance(block.getLocation());
                        Bukkit.getLogger().info("entities distance to block: " + String.valueOf(distance));

                        if (distance < closestDistance && distance < 1.35) {
                            closestDistance = distance;
                            closestItem = (Item) entity;
                        }
                    }
                }

                // If an item was found, teleport it to the target location
                if (closestItem != null) {
                    closestItem.teleport(new Location(Bukkit.getWorld("world"), this.oneBlockLocation.getX() + 0.5, this.oneBlockLocation.getY() + 1, this.oneBlockLocation.getZ() + 0.5));
                    Bukkit.getLogger().info(String.valueOf(closestDistance));
                } else {
                    Bukkit.getLogger().info("no items found?");
                }
            }, 1L);
            if(blocksBroken > 99) {
                blocksBroken = 0;
                stage++;
            }
            Bukkit.getLogger().info("block broken");
        }
    }

    public void spawnNextBlock() {
        Block block = oneBlockLocation.getBlock();

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
                block.setType(Material.END_PORTAL_FRAME);
                break;
            default:
                block.setType(Material.AIR);
                break;
        }
    }

    private Material getSurfaceBlock() {
        Material[] surfaceBlocks = {Material.GRASS_BLOCK, Material.OAK_LOG, Material.BIRCH_LOG};
        return surfaceBlocks[random.nextInt(surfaceBlocks.length)];
    }

    private Material getUndergroundBlock() {
        Material[] undergroundBlocks = {Material.STONE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.LAPIS_ORE};
        return undergroundBlocks[random.nextInt(undergroundBlocks.length)];
    }

    private Material getNetherBlock() {
        Material[] netherBlocks = {Material.NETHERRACK, Material.NETHER_BRICKS, Material.NETHER_QUARTZ_ORE};
        return netherBlocks[random.nextInt(netherBlocks.length)];
    }
}