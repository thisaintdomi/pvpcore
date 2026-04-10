package hu.pvpcore.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import hu.pvpcore.PvPCore;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecentHologramsProvider implements HologramProvider {

    private final PvPCore plugin;
    private final Map<String, String> hologramIds = new HashMap<>();

    public DecentHologramsProvider(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void createLeaderboard(String id, Location location, String[] lines) {
        String dhId = "pvpcore_" + id;
        hologramIds.put(id, dhId);

        List<String> lineList = new ArrayList<>(Arrays.asList(lines));

        if (DHAPI.getHologram(dhId) != null) {
            DHAPI.removeHologram(dhId);
        }

        DHAPI.createHologram(dhId, location, lineList);
    }

    @Override
    public void removeLeaderboard(String id) {
        String dhId = hologramIds.getOrDefault(id, "pvpcore_" + id);
        if (DHAPI.getHologram(dhId) != null) {
            DHAPI.removeHologram(dhId);
        }
        hologramIds.remove(id);
    }

    @Override
    public void shutdown() {
        hologramIds.keySet().forEach(this::removeLeaderboard);
    }
}
