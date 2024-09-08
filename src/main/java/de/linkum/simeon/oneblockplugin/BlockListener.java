package de.linkum.simeon.oneblockplugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.bukkit.Material.*;

public class BlockListener implements Listener {

    private final Main plugin;
    private final Location oneBlockLocation;
    private int stage = 1;
    private int highestStage = 1;
    private int blocksBroken = 0;
    private final Random random = new Random();

    public BlockListener(Main plugin, String worldName) {
        this.plugin = plugin;
        World world = Bukkit.getWorld(worldName);
        this.oneBlockLocation = new Location(world, 0, 100, 0); // Setze hier die Koordinaten des OneBlock
        this.spawnNextBlock(oneBlockLocation.getBlock());

        Command command = new Command("oneblockstage") {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
                if (sender instanceof Player && args.length == 1) {
                    if(Integer.parseInt(args[0]) <= highestStage) {
                        stage = Integer.parseInt(args[0]);
                        blocksBroken = 0;
                        sender.sendMessage("set stage to: " + stage);
                    } else {
                        sender.sendMessage("stage not unlocked yet");
                    }
                }
                return true;
            }
        };

        this.plugin.getServer().getCommandMap().register("oneblockstage", command);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getLocation().getX() != oneBlockLocation.getX() ||
                block.getLocation().getY() != oneBlockLocation.getY() ||
                block.getLocation().getZ() != oneBlockLocation.getZ() ||
                !block.getWorld().getName().equals(oneBlockLocation.getWorld().getName())) {
            return;
        }
        blocksBroken++;

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
        if(blocksBroken > (100 * stage) - 1) {
            blocksBroken = 0;
            stage++;
            highestStage++;
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

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if(event.getWorld().getName().startsWith("customdimension")) {
            for( int x = 0; x < 16; x++ ) {
                for (int y = -64; y < 320; y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = event.getChunk().getBlock(x, y, z);
                        block.setType(Material.AIR);
                    }
                }
            }
        }
        if(event.isNewChunk()) {
            Block b = event.getWorld().getBlockAt(0, 100, 0);
            b.setType(GRASS_BLOCK);
        }
    }
}