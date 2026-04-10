package hu.pvpcore.gui;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitSelectorGUI implements Listener {

    private final PvPCore plugin;
    private static final String TITLE = ColorUtil.color("&8\u2694 Kit Választó");

    public KitSelectorGUI(PvPCore plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Set<String> kitNames = plugin.getKitManager().getKitNames();
        int rows = Math.max(1, (int) Math.ceil(kitNames.size() / 9.0));
        Inventory inv = Bukkit.createInventory(null, rows * 9, TITLE);

        ConfigurationSection kits = plugin.getConfigManager().get("kits").getConfigurationSection("kits");
        if (kits == null) return;

        int slot = 0;
        for (String kitName : kitNames) {
            ConfigurationSection kit = kits.getConfigurationSection(kitName);
            if (kit == null) continue;

            Material icon = parseMaterial(kit.getString("icon", "IRON_SWORD"));
            String displayName = ColorUtil.color(kit.getString("display-name", kitName));

            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            lore.add("");
            if (plugin.getKitManager().isOnCooldown(player, kitName)) {
                long s = plugin.getKitManager().getRemainingCooldown(player, kitName);
                lore.add(ColorUtil.color("&cCooldown: &e" + s + "s"));
            } else {
                lore.add(ColorUtil.color("&aKattints a felszereléshez!"));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(TITLE)) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;
        String displayName = meta.getDisplayName();

        ConfigurationSection kits = plugin.getConfigManager().get("kits").getConfigurationSection("kits");
        if (kits == null) return;

        for (String kitName : plugin.getKitManager().getKitNames()) {
            ConfigurationSection kit = kits.getConfigurationSection(kitName);
            if (kit == null) continue;
            if (ColorUtil.color(kit.getString("display-name", kitName)).equals(displayName)) {
                player.closeInventory();
                plugin.getKitManager().giveKit(player, kitName);
                return;
            }
        }
    }

    private Material parseMaterial(String name) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.IRON_SWORD;
        }
    }
}
