package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import hu.pvpcore.utils.LuckPermsUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private final PvPCore plugin;

    public StatsCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pvpcore.stats")) {
            sender.sendMessage(ColorUtil.color("&cNincs jogosultságod!"));
            return true;
        }

        Player target;
        if (args.length >= 1) {
            if (!sender.hasPermission("pvpcore.stats.others")) {
                sender.sendMessage(ColorUtil.color("&cNincs jogod mások statisztikáját megnézni!"));
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ColorUtil.color("&cJátékos nem található: &e" + args[0]));
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage("Usage: /stats <player>");
            return true;
        }

        PlayerStats stats = plugin.getStatsManager().getStats(target);
        String prestige = plugin.getPrestigeManager().getPrestigeDisplay(stats.getPrestige());
        String prefix = LuckPermsUtil.isAvailable() ? LuckPermsUtil.getPrefix(target) : "";

        sender.sendMessage(ColorUtil.color("&8&m----------------------"));
        sender.sendMessage(ColorUtil.color("&6Statisztikák &8| " + prefix + " &f" + target.getName() + " " + prestige));
        sender.sendMessage(ColorUtil.color("&8&m----------------------"));
        sender.sendMessage(ColorUtil.color("  &7Ölések         &c" + stats.getKills()));
        sender.sendMessage(ColorUtil.color("  &7Halálok        &c" + stats.getDeaths()));
        sender.sendMessage(ColorUtil.color("  &7K/D arány      &e" + stats.getKDR()));
        sender.sendMessage(ColorUtil.color("  &7Jelenlegi sor. &a" + stats.getCurrentStreak()));
        sender.sendMessage(ColorUtil.color("  &7Legjobb sor.   &a" + stats.getBestStreak()));
        sender.sendMessage(ColorUtil.color("  &7Prestige       &6" + stats.getPrestige()));
        sender.sendMessage(ColorUtil.color("  &7Coinok         &6" + stats.getCoins()));
        sender.sendMessage(ColorUtil.color("&8&m----------------------"));
        return true;
    }
}
