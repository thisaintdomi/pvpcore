package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class KitManager {

    private final PvPCore plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public KitManager(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("kits");
    }

    public boolean giveKit(Player player, String kitName) {
        ConfigurationSection kits = cfg().getConfigurationSection("kits");
        if (kits == null || !kits.contains(kitName)) {
            player.sendMessage(ColorUtil.color("&cIsmeretlen kit: &e" + kitName));
            return false;
        }

        if (isOnCooldown(player, kitName) && !player.hasPermission("pvpcore.kit.bypass-cooldown")) {
            long remaining = getRemainingCooldown(player, kitName);
            player.sendMessage(ColorUtil.color("&cVárj még &e" + remaining + " &cmásodpercet!"));
            return false;
        }

        ConfigurationSection kit = kits.getConfigurationSection(kitName);
        if (kit == null) return false;

        PlayerInventory inv = player.getInventory();
        inv.clear();

        applyArmor(inv, kit);
        applyItems(inv, kit);

        int baseCooldown = kit.getInt("cooldown", cfg().getInt("cooldown", 300));
        int reduction = getPrestigeReduction(player);
        int finalCooldown = Math.max(0, baseCooldown - reduction);
        setCooldown(player, kitName, finalCooldown);

        player.sendMessage(ColorUtil.color("&aKit felszerelve: " + ColorUtil.color(kit.getString("display-name", kitName))));
        return true;
    }

    private void applyArmor(PlayerInventory inv, ConfigurationSection kit) {
        setIfPresent(kit, "items.helmet",     m -> inv.setHelmet(new ItemStack(m)));
        setIfPresent(kit, "items.chestplate", m -> inv.setChestplate(new ItemStack(m)));
        setIfPresent(kit, "items.leggings",   m -> inv.setLeggings(new ItemStack(m)));
        setIfPresent(kit, "items.boots",      m -> inv.setBoots(new ItemStack(m)));
    }

    private void applyItems(PlayerInventory inv, ConfigurationSection kit) {
        setIfPresent(kit, "items.sword", m -> inv.addItem(new ItemStack(m)));
        setIfPresent(kit, "items.bow",   m -> inv.addItem(new ItemStack(m)));

        int arrows = kit.getInt("items.arrows", 0);
        int food = kit.getInt("items.food", 0);
        if (arrows > 0) inv.addItem(new ItemStack(Material.ARROW, arrows));
        if (food > 0) inv.addItem(new ItemStack(Material.COOKED_BEEF, food));
    }

    private void setIfPresent(ConfigurationSection kit, String path, java.util.function.Consumer<Material> consumer) {
        String value = kit.getString(path);
        if (value == null) return;
        try {
            consumer.accept(Material.valueOf(value));
        } catch (IllegalArgumentException ignored) {}
    }

    private int getPrestigeReduction(Player player) {
        int prestige = plugin.getStatsManager().getStats(player).getPrestige();
        if (prestige <= 0) return 0;

        ConfigurationSection levels = plugin.getConfigManager().get("prestige").getConfigurationSection("levels");
        if (levels == null) return 0;

        int reduction = 0;
        for (String key : levels.getKeys(false)) {
            int level = Integer.parseInt(key);
            if (level <= prestige) {
                reduction = Math.max(reduction, levels.getInt(key + ".kit-cooldown-reduction", 0));
            }
        }
        return reduction;
    }

    private void setCooldown(Player player, String kitName, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                 .put(kitName, System.currentTimeMillis() + seconds * 1000L);
    }

    public boolean isOnCooldown(Player player, String kitName) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return false;
        Long expiry = map.get(kitName);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    public long getRemainingCooldown(Player player, String kitName) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return 0;
        Long expiry = map.get(kitName);
        if (expiry == null) return 0;
        return Math.max(0, (expiry - System.currentTimeMillis()) / 1000);
    }

    public Set<String> getKitNames() {
        ConfigurationSection kits = cfg().getConfigurationSection("kits");
        return kits != null ? kits.getKeys(false) : Set.of();
    }
}
