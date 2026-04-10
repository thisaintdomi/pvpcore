package hu.pvpcore.listeners;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathDropListener implements Listener {

    private final PvPCore plugin;

    public DeathDropListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("economy");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!cfg().getBoolean("death-drop.enabled", true)) return;

        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) return;

        PlayerStats victimStats = plugin.getStatsManager().getStats(victim);
        long coins = victimStats.getCoins();
        if (coins <= 0) return;

        double pct = cfg().getDouble("death-drop.percentage", 5) / 100.0;
        long dropped = Math.max(1, Math.round(coins * pct));

        plugin.getEconomyManager().removeCoins(victim, dropped);
        plugin.getEconomyManager().addCoins(killer, dropped);

        killer.sendMessage(ColorUtil.color("&aEllopted &6" + dropped + " coint &7" + victim.getName() + "-től!"));
        victim.sendMessage(ColorUtil.color("&cVesztettél &6" + dropped + " coint&c! (&7" + (int)(pct * 100) + "%&c)"));
    }
}
