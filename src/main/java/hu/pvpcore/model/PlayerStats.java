package hu.pvpcore.model;

import java.util.UUID;

public class PlayerStats {

    private final UUID uuid;
    private String name;
    private int kills;
    private int deaths;
    private int currentStreak;
    private int bestStreak;
    private int prestige;
    private long coins;
    private long totalPlaytime;

    public PlayerStats(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.kills = 0;
        this.deaths = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.prestige = 0;
        this.coins = 0;
        this.totalPlaytime = 0;
    }

    public void addKill() {
        kills++;
        currentStreak++;
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
    }

    public void addDeath() {
        deaths++;
        currentStreak = 0;
    }

    public double getKDR() {
        if (deaths == 0) return kills;
        return Math.round((double) kills / deaths * 100.0) / 100.0;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }
    public int getPrestige() { return prestige; }
    public void setPrestige(int prestige) { this.prestige = prestige; }
    public long getCoins() { return coins; }
    public void setCoins(long coins) { this.coins = coins; }
    public long getTotalPlaytime() { return totalPlaytime; }
    public void setTotalPlaytime(long totalPlaytime) { this.totalPlaytime = totalPlaytime; }
}
