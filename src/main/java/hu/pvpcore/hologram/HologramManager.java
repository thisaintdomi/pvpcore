package hu.pvpcore.hologram;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class HologramManager {

    private final PvPCore plugin;
    private final HologramProvider provider;
    private BukkitTask updateTask;

    public HologramManager(PvPCore plugin) {
        this.plugin = plugin;

        if (Bukkit.getPluginManager().getPlugin("DecentHolograms") != null) {
            provider = new DecentHologramsProvider(plugin);
            plugin.getLogger().info("DecentHolograms detected — using DH for holograms.");
        } else {
            provider = new ArmorStandHologramProvider(plugin);
            plugin.getLogger().info("DecentHolograms not found — using built-in ArmorStand holograms.");
        }

        if (cfg().getBoolean("hologram.enabled", true)) {
            startUpdateTask();
        }
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("leaderboard");
    }

    private void startUpdateTask() {
        int interval = cfg().getInt("update-interval", 300);
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateLeaderboard, 100L, interval * 20L);
    }

    public void updateLeaderboard() {
        Location loc = getConfiguredLocation();
        if (loc == null) return;

        List<PlayerStats> top = plugin.getStatsManager().getTopByKills(10);
        String title = ColorUtil.color(cfg().getString("hologram.title", "&6&lTop Játékosok"));
        String format = cfg().getString("hologram.format", "&e#{pos} &f{player} &7- &c{kills} ölés");

        provider.removeLeaderboard("pvpcore_top");

        String[] lines = new String[top.size() + 1];
        lines[0] = title;
        for (int i = 0; i < top.size(); i++) {
            PlayerStats s = top.get(i);
            lines[i + 1] = ColorUtil.color(format
                    .replace("{pos}", String.valueOf(i + 1))
                    .replace("{player}", s.getName())
                    .replace("{kills}", String.valueOf(s.getKills()))
                    .replace("{kdr}", String.valueOf(s.getKDR())));
        }

        provider.createLeaderboard("pvpcore_top", loc, lines);
    }

    private Location getConfiguredLocation() {
        String worldName = cfg().getString("hologram.location.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = cfg().getDouble("hologram.location.x", 0.5);
        double y = cfg().getDouble("hologram.location.y", 65.0);
        double z = cfg().getDouble("hologram.location.z", 0.5);
        return new Location(world, x, y, z);
    }

    public void shutdown() {
        if (updateTask != null) updateTask.cancel();
        provider.removeLeaderboard("pvpcore_top");
        provider.shutdown();
    }
}
