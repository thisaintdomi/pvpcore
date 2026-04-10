package hu.pvpcore.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.CompletableFuture;

public final class LuckPermsUtil {

    private static LuckPerms api;
    private static boolean available = false;

    private LuckPermsUtil() {}

    public static void init() {
        RegisteredServiceProvider<LuckPerms> provider =
                Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
            available = true;
        }
    }

    public static boolean isAvailable() {
        return available;
    }

    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    public static void addPermission(Player player, String permission) {
        if (!available) return;
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;
        user.data().add(Node.builder(permission).build());
        api.getUserManager().saveUser(user);
    }

    public static void removePermission(Player player, String permission) {
        if (!available) return;
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;
        user.data().remove(Node.builder(permission).build());
        api.getUserManager().saveUser(user);
    }

    public static CompletableFuture<Void> addPermissionAsync(Player player, String permission) {
        if (!available) return CompletableFuture.completedFuture(null);
        return api.getUserManager().loadUser(player.getUniqueId()).thenAccept(user -> {
            user.data().add(Node.builder(permission).build());
            api.getUserManager().saveUser(user);
        });
    }

    public static String getPrefix(Player player) {
        if (!available) return "";
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";
        var cachedData = user.getCachedData().getMetaData();
        String prefix = cachedData.getPrefix();
        return prefix != null ? ColorUtil.color(prefix) : "";
    }
}
