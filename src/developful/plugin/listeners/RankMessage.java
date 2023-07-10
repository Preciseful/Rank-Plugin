package developful.plugin.listeners;

import developful.plugin.Devplug;
import developful.plugin.commands.rank.PermManager;
import developful.plugin.commands.rank.Rank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RankMessage implements Listener {
    private final Devplug plugin;

    public RankMessage(Devplug plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.PermissionManager.SetPerm(null, event.getPlayer(), PermManager.DEFAULT_RANK, false);
        plugin.PermissionManager
                .GetRank(event.getPlayer())
                .SetAttachment(plugin, event.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Rank rank = plugin.PermissionManager.GetRank(event.getPlayer());

        plugin.getServer().broadcastMessage(
                String.format("%s[%s]%s %s%s >>%s %s",
                        rank.rankColor,
                        rank,
                        rank.nameColor,
                        event.getPlayer().getDisplayName(),
                        rank.symbolColor,
                        rank.chatColor,
                        event.getMessage()
                )
        );
    }
}
