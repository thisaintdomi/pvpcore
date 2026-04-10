package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrestigeCommand implements CommandExecutor {

    private final PvPCore plugin;

    public PrestigeCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        PlayerStats stats = plugin.getStatsManager().getStats(player);

        if (args.length >= 1 && args[0].equalsIgnoreCase("claim")) {
            plugin.getPrestigeManager().doPrestige(player);
            return true;
        }

        int current = stats.getPrestige();
        int nextRequired = plugin.getPrestigeManager().getNextRequiredKills(player);

        player.sendMessage(ColorUtil.color("&8&m------------------------"));
        player.sendMessage(ColorUtil.color("        &6&lPrestige Info"));
        player.sendMessage(ColorUtil.color("&8&m------------------------"));
        player.sendMessage(ColorUtil.color("  &7Jelenlegi prestige: &6" + current));
        player.sendMessage(ColorUtil.color("  &7Ölések: &c" + stats.getKills()));

        if (nextRequired < 0) {
            player.sendMessage(ColorUtil.color("  &7Elérted a maximális prestige szintet!"));
        } else {
            player.sendMessage(ColorUtil.color("  &7Következő prestige: &e" + nextRequired + " &7ölés szükséges"));
            if (plugin.getPrestigeManager().canPrestige(player)) {
                player.sendMessage(ColorUtil.color("  &a✔ Kész vagy! Írd: &e/prestige claim"));
            } else {
                int remaining = nextRequired - stats.getKills();
                player.sendMessage(ColorUtil.color("  &7Még &c" + remaining + " &7ölés hiányzik."));
            }
        }

        player.sendMessage(ColorUtil.color("&8&m------------------------"));
        return true;
    }
}
