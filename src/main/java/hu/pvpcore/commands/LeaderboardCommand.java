package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.model.PlayerStats;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class LeaderboardCommand implements CommandExecutor {

    private final PvPCore plugin;

    public LeaderboardCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<PlayerStats> top = plugin.getStatsManager().getTopByKills(10);

        sender.sendMessage(ColorUtil.color("&8&m-----------------------------"));
        sender.sendMessage(ColorUtil.color("      &6&lTop 10 PvP Játékos"));
        sender.sendMessage(ColorUtil.color("&8&m-----------------------------"));

        if (top.isEmpty()) {
            sender.sendMessage(ColorUtil.color("&7Még nincs adat."));
        } else {
            for (int i = 0; i < top.size(); i++) {
                PlayerStats s = top.get(i);
                String prestige = plugin.getPrestigeManager().getPrestigeDisplay(s.getPrestige());
                String pos = getPositionColor(i + 1) + "#" + (i + 1);
                sender.sendMessage(ColorUtil.color(pos + " &f" + prestige + " " + s.getName()
                        + " &7- &c" + s.getKills() + " ölés &7| K/D: &e" + s.getKDR()));
            }
        }

        sender.sendMessage(ColorUtil.color("&8&m-----------------------------"));
        return true;
    }

    private String getPositionColor(int pos) {
        return switch (pos) {
            case 1 -> "&6&l";
            case 2 -> "&f&l";
            case 3 -> "&c&l";
            default -> "&7";
        };
    }
}
