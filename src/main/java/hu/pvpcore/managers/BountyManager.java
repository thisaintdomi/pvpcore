package hu.pvpcore.managers;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {

    private final PvPCore plugin;
    private final Map<UUID, Long> bounties = new HashMap<>();
    private final Map<UUID, UUID> bountySetters = new HashMap<>();

    public BountyManager(PvPCore plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfigManager().get("bounty");
    }

    public boolean setBounty(Player setter, Player target, long amount) {
        long min = cfg().getLong("min-amount", 100);
        long max = cfg().getLong("max-amount", 100000);
        if (amount < min || amount > max) return false;
        if (setter.equals(target)) return false;
        if (plugin.getEconomyManager().getCoins(setter) < amount) return false;

        plugin.getEconomyManager().removeCoins(setter, amount);
        long existing = bounties.getOrDefault(target.getUniqueId(), 0L);
        bounties.put(target.getUniqueId(), existing + amount);
        bountySetters.put(target.getUniqueId(), setter.getUniqueId());

        if (cfg().getBoolean("broadcast-on-set", true)) {
            String msg = cfg().getString("set-message", "&e{setter} &7fejdíjat tűzött ki &c{target} fejére: &6{amount} coin");
            Bukkit.broadcastMessage(ColorUtil.color(msg
                    .replace("{setter}", setter.getName())
                    .replace("{target}", target.getName())
                    .replace("{amount}", String.valueOf(existing + amount))));
        }
        return true;
    }

    public void collectBounty(Player killer, Player victim) {
        Long amount = bounties.remove(victim.getUniqueId());
        if (amount == null || amount <= 0) return;
        bountySetters.remove(victim.getUniqueId());
        plugin.getEconomyManager().addCoins(killer, amount);

        if (cfg().getBoolean("broadcast-on-collect", true)) {
            String msg = cfg().getString("collect-message", "&e{killer} &7beváltotta &c{target} &7fejdíját! &6+{amount} coin");
            Bukkit.broadcastMessage(ColorUtil.color(msg
                    .replace("{killer}", killer.getName())
                    .replace("{target}", victim.getName())
                    .replace("{amount}", String.valueOf(amount))));
        }
        killer.sendMessage(ColorUtil.color("&aFejdíj begyűjtve! &6+" + amount + " coin"));
    }

    public long getBounty(UUID uuid) {
        return bounties.getOrDefault(uuid, 0L);
    }

    public boolean hasBounty(UUID uuid) {
        return bounties.containsKey(uuid) && bounties.get(uuid) > 0;
    }
}
