package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PvPCoreCommand implements CommandExecutor {

    private final PvPCore plugin;

    public PvPCoreCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pvpcore.admin")) {
            sender.sendMessage(ColorUtil.color("&cNincs jogosultságod!"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reload();
                sender.sendMessage(ColorUtil.color("&aPvPCore újratöltve. &7(config, killstreak, kits, prestige, stb.)"));
            }
            case "hologram", "holo" -> {
                plugin.getHologramManager().updateLeaderboard();
                sender.sendMessage(ColorUtil.color("&aLeaderboard hologram frissítve."));
            }
            case "arena" -> {
                if (args.length >= 2 && args[1].equalsIgnoreCase("refill")) {
                    if (!sender.hasPermission("pvpcore.arena.refill")) {
                        sender.sendMessage(ColorUtil.color("&cNincs jogosultságod!"));
                        return true;
                    }
                    plugin.getArenaManager().refillAllChests();
                    sender.sendMessage(ColorUtil.color("&aArena ládák feltöltve."));
                } else {
                    sender.sendMessage(ColorUtil.color("&cHasználat: /pvpcore arena refill"));
                }
            }
            case "saveall" -> {
                plugin.getStatsManager().saveAll();
                sender.sendMessage(ColorUtil.color("&aMindenki adata mentve."));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.color("&8&m--------------------------"));
        sender.sendMessage(ColorUtil.color("    &6&lPvPCore &7v" + plugin.getDescription().getVersion()));
        sender.sendMessage(ColorUtil.color("&8&m--------------------------"));
        sender.sendMessage(ColorUtil.color("  &e/pvc reload &8- &7Minden config újratölt"));
        sender.sendMessage(ColorUtil.color("  &e/pvc holo &8- &7Hologram kézi frissítés"));
        sender.sendMessage(ColorUtil.color("  &e/pvc arena refill &8- &7Ládák feltöltése"));
        sender.sendMessage(ColorUtil.color("  &e/pvc saveall &8- &7Adatok mentése"));
        sender.sendMessage(ColorUtil.color("&8&m--------------------------"));
    }
}
