package de.linkum.simeon.oneblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

import static java.lang.Double.MAX_VALUE;
import static org.bukkit.Material.*;

public class BlockListener implements Listener {

    private final Main plugin;
    private final Location oneBlockLocation;
    private int stage = 1;
    private int blocksBroken = 0;
    private final Random random = new Random();

    public BlockListener(Main plugin) {
        this.plugin = plugin;
        World world = Bukkit.getWorld("world");
        this.oneBlockLocation = new Location(world, 0, 100, 0); // Setze hier die Koordinaten des OneBlock
        this.spawnNextBlock(oneBlockLocation.getBlock());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Bukkit.getLogger().info("block broken");
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Bukkit.getLogger().info(String.valueOf(block.getLocation()));

        Location location = new Location(block.getLocation().getWorld(), 0, 100, 0);

        if (!(block.getLocation().equals(location))) {
            return;
        }
        // event.setDropItems(false); // Verhindert, dass der Block normale Drops gibt
        blocksBroken++;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            this.spawnNextBlock(block);
            // Find the nearest item to the block and teleport it to the target location
            double closestDistance = 0;
            Item closestItem = null;

            for (Entity entity : block.getLocation().getWorld().getNearbyEntities(block.getLocation(), 10, 10, 10)) {
                if (!(entity instanceof Item && !entity.isDead())) {
                    return;
                }
                Bukkit.getLogger().info("entity found");
                double distance = entity.getLocation().distance(block.getLocation());
                Bukkit.getLogger().info("entities distance to block: " + distance);

                if (!(distance < MAX_VALUE && distance < 1.35)) {
                    return;
                }
                closestDistance = distance;
                closestItem = (Item) entity;
            }

            // If an item was found, teleport it to the target location
            if (closestItem != null) {
                World world = Bukkit.getWorld("world");
                int positionX = (int) (this.oneBlockLocation.getX() + 0.5);
                int positionY = (int) (this.oneBlockLocation.getY() + 1);
                int positionZ = (int) (this.oneBlockLocation.getZ() + 0.5);
                closestItem.teleport(new Location(world, positionX, positionY, positionZ));
                Bukkit.getLogger().info(String.valueOf(closestDistance));
            } else {
                Bukkit.getLogger().info("no items found?");
            }
        }, 1L);
        if(blocksBroken > 99) {
            blocksBroken = 0;
            stage++;
        }
        Bukkit.getLogger().info("block broken at correct position");
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