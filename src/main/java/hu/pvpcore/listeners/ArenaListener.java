package hu.pvpcore.listeners;

import hu.pvpcore.PvPCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ArenaListener implements Listener {

    private final PvPCore plugin;

    public ArenaListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        plugin.getArenaManager().handlePlayerRespawn(event.getPlayer());
    }
}
