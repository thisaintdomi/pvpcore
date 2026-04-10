package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconomyManager {

    private final PvPCore plugin;

    public EconomyManager(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("economy");
    }

    public long getCoins(Player player) {
        return plugin.getStatsManager().getStats(player).getCoins();
    }

    public long getCoins(UUID uuid) {
        PlayerStats stats = plugin.getStatsManager().getStats(uuid);
        return stats != null ? stats.getCoins() : 0;
    }

    public void addCoins(Player player, long amount) {
        PlayerStats stats = plugin.getStatsManager().getStats(player);
        stats.setCoins(stats.getCoins() + amount);
    }

    public boolean removeCoins(Player player, long amount) {
        PlayerStats stats = plugin.getStatsManager().getStats(player);
        if (stats.getCoins() < amount) return false;
        stats.setCoins(stats.getCoins() - amount);
        return true;
    }

    public void rewardKill(Player killer, Player victim) {
        long base = cfg().getLong("kill-reward", 10);
        double multiplier = cfg().getDouble("streak-bonus-multiplier", 1.5);
        int streak = plugin.getStatsManager().getStats(killer).getCurrentStreak();

        long reward = base;
        if (streak >= 10) {
            reward = Math.round(base * multiplier * 2);
        } else if (streak >= 5) {
            reward = Math.round(base * multiplier);
        }

        if (plugin.getBountyManager().hasBounty(victim.getUniqueId())) {
            plugin.getBountyManager().collectBounty(killer, victim);
        }

        addCoins(killer, reward);
        killer.sendMessage(ColorUtil.color("&a+&6" + reward + " coin &7(ölés jutalom)"));
    }
}
