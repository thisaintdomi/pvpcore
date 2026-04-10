package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTagManager {

    private final PvPCore plugin;
    private final Map<UUID, BukkitTask> taggedPlayers = new HashMap<>();
    private final Map<UUID, Long> tagExpiry = new HashMap<>();

    public CombatTagManager(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("combattag");
    }

    public void tag(Player player, Player opponent) {
        if (!cfg().getBoolean("enabled", true)) return;
        int duration = cfg().getInt("duration", 15);

        tagExpiry.put(player.getUniqueId(), System.currentTimeMillis() + duration * 1000L);
        tagExpiry.put(opponent.getUniqueId(), System.currentTimeMillis() + duration * 1000L);

        scheduleUntag(player, duration);
        scheduleUntag(opponent, duration);

        String msg = cfg().getString("tag-message", "&cHarcban vagy! {seconds} mp-ig nem léphetsz ki.");
        player.sendMessage(ColorUtil.color(msg.replace("{seconds}", String.valueOf(duration))));
        opponent.sendMessage(ColorUtil.color(msg.replace("{seconds}", String.valueOf(duration))));
    }

    private void scheduleUntag(Player player, int seconds) {
        BukkitTask existing = taggedPlayers.get(player.getUniqueId());
        if (existing != null) existing.cancel();

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            taggedPlayers.remove(player.getUniqueId());
            tagExpiry.remove(player.getUniqueId());
            if (player.isOnline()) {
                player.sendMessage(ColorUtil.color(cfg().getString("untag-message", "&aKilépési tilalmad lejárt.")));
            }
        }, seconds * 20L);

        taggedPlayers.put(player.getUniqueId(), task);
    }

    public boolean isTagged(Player player) {
        return taggedPlayers.containsKey(player.getUniqueId());
    }

    public long getRemainingSeconds(Player player) {
        Long expiry = tagExpiry.get(player.getUniqueId());
        if (expiry == null) return 0;
        return Math.max(0, (expiry - System.currentTimeMillis()) / 1000);
    }

    public void handleQuit(Player player) {
        if (!isTagged(player)) return;
        if (!cfg().getBoolean("punish-logout", true)) return;
        String command = cfg().getString("logout-command", "kill");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command + " " + player.getName());
        BukkitTask task = taggedPlayers.remove(player.getUniqueId());
        if (task != null) task.cancel();
        tagExpiry.remove(player.getUniqueId());
    }

    public void removeTag(UUID uuid) {
        BukkitTask task = taggedPlayers.remove(uuid);
        if (task != null) task.cancel();
        tagExpiry.remove(uuid);
    }

    public void shutdown() {
        taggedPlayers.values().forEach(BukkitTask::cancel);
        taggedPlayers.clear();
        tagExpiry.clear();
    }
}
