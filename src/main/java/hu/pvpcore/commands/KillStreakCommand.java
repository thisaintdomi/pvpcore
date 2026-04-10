package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillStreakCommand implements CommandExecutor {

    private final PvPCore plugin;

    public KillStreakCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        PlayerStats stats = plugin.getStatsManager().getStats(player);
        int streak = stats.getCurrentStreak();
        int best = stats.getBestStreak();

        player.sendMessage(ColorUtil.color("&6Jelenlegi sorozatod: &c" + streak));
        player.sendMessage(ColorUtil.color("&6Legjobb sorozatod: &c" + best));

        if (streak >= 5) {
            player.sendMessage(ColorUtil.color("&aEgy jó sorozatot futtatsz! Tartsd meg!"));
        }
        return true;
    }
}
