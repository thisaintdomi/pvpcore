package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Location;

public class ArenaManager {

    private final PvPCore plugin;
    private final List<Location> registeredChests = new ArrayList<>();
    private BukkitTask refillTask;

    private static final Material[] LOOT_POOL = {
        Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.BOW,
        Material.ARROW, Material.GOLDEN_APPLE, Material.COOKED_BEEF,
        Material.IRON_HELMET, Material.IRON_CHESTPLATE,
        Material.DIAMOND_HELMET, Material.ENDER_PEARL
    };

    public ArenaManager(PvPCore plugin) {
        this.plugin = plugin;
        if (cfg().getBoolean("enabled", true)) {
            startRefillTask();
        }
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("arena");
    }

    private void startRefillTask() {
        int interval = cfg().getInt("chest-refill-interval", 120);
        refillTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refillAllChests, interval * 20L, interval * 20L);
    }

    public void registerChest(Location loc) {
        if (!registeredChests.contains(loc)) registeredChests.add(loc);
    }

    public void refillAllChests() {
        int filled = 0;
        Iterator<Location> it = registeredChests.iterator();
        while (it.hasNext()) {
            Location loc = it.next();
            Block block = loc.getBlock();
            if (block.getType() != Material.CHEST) {
                it.remove();
                continue;
            }
            fillChest(((Chest) block.getState()).getInventory());
            filled++;
        }
        if (filled > 0) {
            Bukkit.broadcastMessage(ColorUtil.color("&6[Arena] &eA ládák feltöltve! &7(" + filled + " láda)"));
        }
    }

    private void fillChest(Inventory inv) {
        inv.clear();
        Random random = new Random();
        int count = 3 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            Material mat = LOOT_POOL[random.nextInt(LOOT_POOL.length)];
            int amount = switch (mat) {
                case ARROW -> 16 + random.nextInt(32);
                case COOKED_BEEF -> 8 + random.nextInt(24);
                default -> 1;
            };
            int slot = random.nextInt(inv.getSize());
            if (inv.getItem(slot) == null) inv.setItem(slot, new ItemStack(mat, amount));
        }
    }

    public void handlePlayerRespawn(Player player) {
        if (cfg().getBoolean("auto-reset", true)) {
            player.sendMessage(ColorUtil.color("&7Visszahelyezve az arénába."));
        }
    }

    public void shutdown() {
        if (refillTask != null) refillTask.cancel();
    }
}
