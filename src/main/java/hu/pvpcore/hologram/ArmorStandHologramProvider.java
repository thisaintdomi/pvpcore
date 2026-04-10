package hu.pvpcore.hologram;

import hu.pvpcore.PvPCore;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorStandHologramProvider implements HologramProvider {

    private final PvPCore plugin;
    private final Map<String, List<ArmorStand>> stands = new HashMap<>();

    private static final double LINE_SPACING = 0.28;

    public ArmorStandHologramProvider(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void createLeaderboard(String id, Location base, String[] lines) {
        List<ArmorStand> existing = stands.remove(id);
        if (existing != null) existing.forEach(ArmorStand::remove);

        List<ArmorStand> created = new ArrayList<>();
        double y = base.getY() + (lines.length - 1) * LINE_SPACING;

        for (String line : lines) {
            Location loc = base.clone();
            loc.setY(y);

            ArmorStand stand = (ArmorStand) base.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomNameVisible(true);
            stand.setCustomName(line);
            stand.setSmall(true);
            stand.setInvulnerable(true);
            stand.setPersistent(false);

            created.add(stand);
            y -= LINE_SPACING;
        }

        stands.put(id, created);
    }

    @Override
    public void removeLeaderboard(String id) {
        List<ArmorStand> list = stands.remove(id);
        if (list != null) list.forEach(ArmorStand::remove);
    }

    @Override
    public void shutdown() {
        stands.values().forEach(list -> list.forEach(ArmorStand::remove));
        stands.clear();
    }
}
