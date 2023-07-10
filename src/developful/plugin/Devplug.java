package developful.plugin;

import developful.plugin.commands.rank.PermManager;
import developful.plugin.listeners.RankMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class Devplug extends JavaPlugin {
    public PermManager PermissionManager;

    @Override
    public void onEnable() {
        System.out.println("hell yeah");
        PermissionManager = new PermManager(this);
        new RankMessage(this);
    }
}
