package developful.plugin.extensions;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public class ErrorExtension {
    public static void Error( CommandSender player, String msg) {
        player.sendMessage(ChatColor.RED + msg);
    }
}
