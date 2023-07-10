package developful.plugin.commands.rank;

import developful.plugin.Devplug;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Rank {
    public String name;
    public ChatColor rankColor, nameColor, chatColor, symbolColor;
    public ArrayList<String> permissions = new ArrayList<>();
    private final HashMap<UUID, PermissionAttachment> attachmentHashMap = new HashMap<>();

    public boolean hasPermission(String name) {
        return permissions.contains(name);
    }

    public boolean hasPermission(Player sender, String name) {
        return permissions.contains(name) || sender.isOp();
    }

    public void RemoveAttachment(Player player) {
        player.removeAttachment(attachmentHashMap.get(player.getUniqueId()));
        attachmentHashMap.remove(player.getUniqueId());
        player.recalculatePermissions();
        player.updateCommands();
    }

    public void SetAttachment(Devplug plugin, Player player) {
        attachmentHashMap.put(player.getUniqueId(), RefreshAttachment(player.addAttachment(plugin)));
        player.recalculatePermissions();
        player.updateCommands();
    }

    private PermissionAttachment RefreshAttachment(PermissionAttachment attachment) {
        for (String perm : permissions)
            attachment.setPermission(perm, true);

        return attachment;
    }

    @Override
    public String toString() {
        return name;
    }
}
