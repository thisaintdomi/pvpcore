package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PrestigeManager {

    private final PvPCore plugin;

    public PrestigeManager(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("prestige");
    }

    public boolean canPrestige(Player player) {
        if (!cfg().getBoolean("enabled", true)) return false;

        PlayerStats stats = plugin.getStatsManager().getStats(player);
        ConfigurationSection level = cfg().getConfigurationSection("levels." + (stats.getPrestige() + 1));
        if (level == null) return false;

        return stats.getKills() >= level.getInt("required-kills", Integer.MAX_VALUE);
    }

    public boolean doPrestige(Player player) {
        if (!canPrestige(player)) {
            player.sendMessage(ColorUtil.color("&cNem teljesíted a prestige feltételeit."));
            return false;
        }

        PlayerStats stats = plugin.getStatsManager().getStats(player);
        int next = stats.getPrestige() + 1;
        ConfigurationSection level = cfg().getConfigurationSection("levels." + next);

        long reward = level.getLong("reward-coins", 0);
        String display = ColorUtil.color(level.getString("display", "[" + next + "]"));

        stats.setPrestige(next);
        stats.setKills(0);
        stats.setDeaths(0);
        stats.setCurrentStreak(0);
        plugin.getEconomyManager().addCoins(player, reward);

        Bukkit.broadcastMessage(ColorUtil.color(
                "&6" + display + " &e" + player.getName() + " &7elérte a &6" + next + ". prestige &7szintet!"));
        player.sendMessage(ColorUtil.color("&aPrestige jutalom: &6+" + reward + " coin"));
        return true;
    }

    public String getPrestigeDisplay(int prestige) {
        if (prestige <= 0) return "";
        ConfigurationSection level = cfg().getConfigurationSection("levels." + prestige);
        if (level == null) return "[" + prestige + "]";
        return ColorUtil.color(level.getString("display", "[" + prestige + "]"));
    }

    public int getNextRequiredKills(Player player) {
        PlayerStats stats = plugin.getStatsManager().getStats(player);
        ConfigurationSection level = cfg().getConfigurationSection("levels." + (stats.getPrestige() + 1));
        if (level == null) return -1;
        return level.getInt("required-kills", -1);
    }
}
