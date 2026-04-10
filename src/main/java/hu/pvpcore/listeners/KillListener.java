package hu.pvpcore.listeners;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {

    private final PvPCore plugin;

    public KillListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        PlayerStats victimStats = plugin.getStatsManager().getStats(victim);
        int oldStreak = victimStats.getCurrentStreak();
        victimStats.addDeath();

        plugin.getKillStreakManager().broadcastEndedStreak(victim, oldStreak);
        plugin.getCombatTagManager().removeTag(victim.getUniqueId());

        if (killer == null || killer.equals(victim)) return;

        PlayerStats killerStats = plugin.getStatsManager().getStats(killer);
        killerStats.addKill();

        plugin.getKillStreakManager().onKill(killer, victim);
        plugin.getEconomyManager().rewardKill(killer, victim);
        plugin.getCombatTagManager().removeTag(killer.getUniqueId());
    }
}
