package hu.pvpcore.commands;

import hu.pvpcore.PvPCore;
import hu.pvpcore.gui.KitSelectorGUI;
import hu.pvpcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private final PvPCore plugin;
    private final KitSelectorGUI gui;

    public KitCommand(PvPCore plugin) {
        this.plugin = plugin;
        this.gui = new KitSelectorGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            gui.open(player);
            return true;
        }

        String kitName = args[0].toLowerCase();
        plugin.getKitManager().giveKit(player, kitName);
        return true;
    }
}
