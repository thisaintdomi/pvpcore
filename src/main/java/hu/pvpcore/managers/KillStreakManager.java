package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KillStreakManager {

    private final PvPCore plugin;

    public KillStreakManager(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("killstreak");
    }

    public void onKill(Player killer, Player victim) {
        if (!cfg().getBoolean("enabled", true)) return;
        int streak = plugin.getStatsManager().getStats(killer).getCurrentStreak();
        checkMilestone(killer, streak);
    }

    private void checkMilestone(Player player, int streak) {
        ConfigurationSection milestones = cfg().getConfigurationSection("milestones");
        if (milestones == null) return;

        for (String key : milestones.getKeys(false)) {
            int threshold = Integer.parseInt(key);
            if (streak != threshold) continue;

            String msg = milestones.getString(key + ".message", "");
            String soundName = milestones.getString(key + ".sound", "");
            int coins = milestones.getInt(key + ".coins", 0);

            Bukkit.broadcastMessage(ColorUtil.color(msg.replace("{player}", player.getName())));

            if (!soundName.isEmpty()) {
                try {
                    Sound sound = Sound.valueOf(soundName);
                    player.getWorld().playSound(player.getLocation(), sound, 1f, 1f);
                } catch (IllegalArgumentException ignored) {}
            }

            if (coins > 0) plugin.getEconomyManager().addCoins(player, coins);
        }
    }

    public void broadcastEndedStreak(Player player, int streak) {
        int threshold = cfg().getInt("broadcast-threshold", 5);
        if (streak < threshold) return;

        String msg = cfg().getString("ended-broadcast", "&7{player} sorozata megtört! ({streak} ölés)");
        Bukkit.broadcastMessage(ColorUtil.color(msg
                .replace("{player}", player.getName())
                .replace("{streak}", String.valueOf(streak))));
    }
}
