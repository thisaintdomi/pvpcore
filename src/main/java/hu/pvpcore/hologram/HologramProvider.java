package hu.pvpcore.hologram;

import org.bukkit.Location;

public interface HologramProvider {
    void createLeaderboard(String id, Location location, String[] lines);
    void removeLeaderboard(String id);
    void shutdown();
}
