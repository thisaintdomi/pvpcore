package hu.pvpcore.config;

import hu.pvpcore.PvPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final PvPCore plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();

    private static final String[] FILES = {
        "combattag", "killstreak", "bounty", "kits", "prestige", "economy", "arena", "leaderboard"
    };

    public ConfigManager(PvPCore plugin) {
        this.plugin = plugin;
        loadAll();
    }

    private void loadAll() {
        for (String name : FILES) {
            load(name);
        }
    }

    private void load(String name) {
        File file = new File(plugin.getDataFolder(), name + ".yml");
        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        configs.put(name, YamlConfiguration.loadConfiguration(file));
    }

    public void reloadAll() {
        configs.clear();
        loadAll();
    }

    public FileConfiguration get(String name) {
        return configs.getOrDefault(name, plugin.getConfig());
    }
}
