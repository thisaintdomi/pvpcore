package hu.pvpcore.listeners;

import hu.pvpcore.PvPCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTagListener implements Listener {

    private final PvPCore plugin;

    public CombatTagListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        plugin.getCombatTagManager().tag(attacker, victim);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getCombatTagManager().handleQuit(event.getPlayer());
        plugin.getStatsManager().unload(event.getPlayer().getUniqueId());
    }
}
