package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StatsManager {

    private final PvPCore plugin;
    private final Map<UUID, PlayerStats> cache = new HashMap<>();
    private final File dataFolder;

    public StatsManager(PvPCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public PlayerStats getStats(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), uuid -> {
            PlayerStats stats = loadFromDisk(uuid, player.getName());
            stats.setName(player.getName());
            return stats;
        });
    }

    public PlayerStats getStats(UUID uuid) {
        return cache.get(uuid);
    }

    private PlayerStats loadFromDisk(UUID uuid, String fallbackName) {
        File file = new File(dataFolder, uuid + ".yml");
        if (!file.exists()) return new PlayerStats(uuid, fallbackName);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        PlayerStats stats = new PlayerStats(uuid, cfg.getString("name", fallbackName));
        stats.setKills(cfg.getInt("kills", 0));
        stats.setDeaths(cfg.getInt("deaths", 0));
        stats.setCurrentStreak(cfg.getInt("currentStreak", 0));
        stats.setBestStreak(cfg.getInt("bestStreak", 0));
        stats.setPrestige(cfg.getInt("prestige", 0));
        stats.setCoins(cfg.getLong("coins", 0));
        stats.setTotalPlaytime(cfg.getLong("playtime", 0));
        return stats;
    }

    public void saveToDisk(PlayerStats stats) {
        File file = new File(dataFolder, stats.getUuid() + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("name", stats.getName());
        cfg.set("kills", stats.getKills());
        cfg.set("deaths", stats.getDeaths());
        cfg.set("currentStreak", stats.getCurrentStreak());
        cfg.set("bestStreak", stats.getBestStreak());
        cfg.set("prestige", stats.getPrestige());
        cfg.set("coins", stats.getCoins());
        cfg.set("playtime", stats.getTotalPlaytime());
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save stats for " + stats.getUuid());
        }
    }

    public void saveAll() {
        cache.values().forEach(this::saveToDisk);
    }

    public void unload(UUID uuid) {
        PlayerStats stats = cache.remove(uuid);
        if (stats != null) saveToDisk(stats);
    }

    public List<PlayerStats> getTopByKills(int limit) {
        List<PlayerStats> all = new ArrayList<>(cache.values());

        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            Set<UUID> cached = cache.keySet();
            for (File f : files) {
                try {
                    UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
                    if (!cached.contains(uuid)) {
                        all.add(loadFromDisk(uuid, "Unknown"));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return all.stream()
                .sorted(Comparator.comparingInt(PlayerStats::getKills).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
