package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCommand implements CommandExecutor {

    private final PvPCore plugin;

    public BountyCommand(PvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ColorUtil.color("&cHasználat: /bounty <játékos> <összeg>"));
            player.sendMessage(ColorUtil.color("&cPélda: /bounty Steve 500"));
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ColorUtil.color("&cJátékos nem található."));
                return true;
            }
            long bounty = plugin.getBountyManager().getBounty(target.getUniqueId());
            if (bounty > 0) {
                player.sendMessage(ColorUtil.color("&e" + target.getName() + " &7fejdíja: &6" + bounty + " coin"));
            } else {
                player.sendMessage(ColorUtil.color("&e" + target.getName() + " &7fejére nincs fejdíj kitűzve."));
            }
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ColorUtil.color("&cJátékos nem található."));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtil.color("&cÉrvénytelen összeg."));
            return true;
        }

        boolean success = plugin.getBountyManager().setBounty(player, target, amount);
        if (!success) {
            long min = plugin.getConfig().getLong("bounty.min-amount", 100);
            long max = plugin.getConfig().getLong("bounty.max-amount", 100000);
            long coins = plugin.getEconomyManager().getCoins(player);

            if (coins < amount) {
                player.sendMessage(ColorUtil.color("&cNincs elég coinjaid! (&6" + coins + "&c / &6" + amount + "&c)"));
            } else if (amount < min || amount > max) {
                player.sendMessage(ColorUtil.color("&cAz összeg " + min + " és " + max + " között kell lennie."));
            } else {
                player.sendMessage(ColorUtil.color("&cNem tudtad beállítani a fejdíjat."));
            }
        }
        return true;
    }
}
