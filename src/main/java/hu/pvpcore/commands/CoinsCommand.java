package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand implements CommandExecutor {

    private final PvPCore plugin;

    public CoinsCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Usage: /coins <player>");
                return true;
            }
            long coins = plugin.getEconomyManager().getCoins(player);
            player.sendMessage(ColorUtil.color("&6Coinjaid: &e" + coins));
            return true;
        }

        if (args.length >= 2 && sender.hasPermission("pvpcore.admin")) {
            String sub = args[0].toLowerCase();
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ColorUtil.color("&cJátékos nem található."));
                return true;
            }

            if ((sub.equals("add") || sub.equals("remove") || sub.equals("set")) && args.length >= 3) {
                long amount;
                try {
                    amount = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.color("&cÉrvénytelen összeg."));
                    return true;
                }

                switch (sub) {
                    case "add" -> {
                        plugin.getEconomyManager().addCoins(target, amount);
                        sender.sendMessage(ColorUtil.color("&aHozzáadva &6" + amount + " coin &a-> " + target.getName()));
                        target.sendMessage(ColorUtil.color("&aKaptál &6" + amount + " coint &7(admin)"));
                    }
                    case "remove" -> {
                        boolean ok = plugin.getEconomyManager().removeCoins(target, amount);
                        if (!ok) sender.sendMessage(ColorUtil.color("&cNincs elég coinja."));
                        else sender.sendMessage(ColorUtil.color("&cElvéve &6" + amount + " coin &c<- " + target.getName()));
                    }
                    case "set" -> {
                        plugin.getStatsManager().getStats(target).setCoins(amount);
                        sender.sendMessage(ColorUtil.color("&e" + target.getName() + " &7coinjai beállítva: &6" + amount));
                    }
                }
                return true;
            }

            long coins = plugin.getEconomyManager().getCoins(target);
            sender.sendMessage(ColorUtil.color("&e" + target.getName() + " &7coinjai: &6" + coins));
            return true;
        }

        if (sender instanceof Player player) {
            long coins = plugin.getEconomyManager().getCoins(player);
            player.sendMessage(ColorUtil.color("&6Coinjaid: &e" + coins));
        }
        return true;
    }
}
