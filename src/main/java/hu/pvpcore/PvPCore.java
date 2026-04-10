package hu.pvpcore;

import hu.pvpcore.commands.*;
import hu.pvpcore.config.ConfigManager;
import hu.pvpcore.hologram.HologramManager;
import hu.pvpcore.listeners.*;
import hu.pvpcore.managers.*;
import hu.pvpcore.utils.LuckPermsUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class PvPCore extends JavaPlugin {

    private static PvPCore instance;

    private ConfigManager configManager;
    private CombatTagManager combatTagManager;
    private KillStreakManager killStreakManager;
    private BountyManager bountyManager;
    private StatsManager statsManager;
    private KitManager kitManager;
    private PrestigeManager prestigeManager;
    private EconomyManager economyManager;
    private ArenaManager arenaManager;
    private HologramManager hologramManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        LuckPermsUtil.init();

        setupManagers();
        setupListeners();
        setupCommands();

        getLogger().info("PvPCore " + getDescription().getVersion() + " loaded.");
        if (LuckPermsUtil.isAvailable()) {
            getLogger().info("LuckPerms found — permission integration active.");
        }
    }

    @Override
    public void onDisable() {
        if (combatTagManager != null) combatTagManager.shutdown();
        if (hologramManager != null) hologramManager.shutdown();
        if (statsManager != null) statsManager.saveAll();
        getLogger().info("PvPCore disabled, all data saved.");
    }

    private void setupManagers() {
        statsManager = new StatsManager(this);
        economyManager = new EconomyManager(this);
        combatTagManager = new CombatTagManager(this);
        killStreakManager = new KillStreakManager(this);
        bountyManager = new BountyManager(this);
        kitManager = new KitManager(this);
        prestigeManager = new PrestigeManager(this);
        arenaManager = new ArenaManager(this);
        hologramManager = new HologramManager(this);
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new CombatTagListener(this), this);
        getServer().getPluginManager().registerEvents(new KillListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathDropListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
    }

    private void setupCommands() {
        getCommand("pvpcore").setExecutor(new PvPCoreCommand(this));
        getCommand("killstreak").setExecutor(new KillStreakCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("bounty").setExecutor(new BountyCommand(this));
        getCommand("prestige").setExecutor(new PrestigeCommand(this));
        getCommand("coins").setExecutor(new CoinsCommand(this));
    }

    public void reload() {
        reloadConfig();
        configManager.reloadAll();
        hologramManager.updateLeaderboard();
    }

    public static PvPCore getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public CombatTagManager getCombatTagManager() { return combatTagManager; }
    public KillStreakManager getKillStreakManager() { return killStreakManager; }
    public BountyManager getBountyManager() { return bountyManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public KitManager getKitManager() { return kitManager; }
    public PrestigeManager getPrestigeManager() { return prestigeManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public HologramManager getHologramManager() { return hologramManager; }
}
