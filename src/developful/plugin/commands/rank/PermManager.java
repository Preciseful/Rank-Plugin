package developful.plugin.commands.rank;

import developful.plugin.Devplug;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import developful.plugin.extensions.ErrorExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PermManager implements CommandExecutor {
    private final String permsPath = "F:\\a\\Minecraft\\plugin\\src\\developful\\plugin\\commands\\rank\\perms.yml",
                ranksPath = "F:\\a\\Minecraft\\plugin\\src\\developful\\plugin\\commands\\rank\\ranks.yml";

    public static final String DEFAULT_RANK = "Player";
    private final Devplug plugin;
    public Ranks ranks;
    public HashMap<UUID, Rank> Permissions = new HashMap<>();

    public PermManager(Devplug plugin) {
        this.plugin = plugin;
        LoadRanks();
        LoadPerms();
        Objects.requireNonNull(plugin.getCommand("setperm")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (strings.length) {
            case 0 -> {
                ErrorExtension.Error(commandSender, "Expected player and rank.");
                return false;
            }
            case 1 -> {
                ErrorExtension.Error(commandSender, "Expected rank.");
                return false;
            }

            default -> {
                if(strings.length == 2)
                    break;
                ErrorExtension.Error(commandSender, "Overloaded command.");
                return false;
            }
        }

        Player player = plugin.getServer().getPlayer(strings[0]);
        if(player == null) {
            ErrorExtension.Error(commandSender, "Invalid player.");
            return false;
        }

        String rank = strings[1];
        if(!(commandSender instanceof Player Player))
            return SetPerm(null, player, rank, true);
        return SetPerm(Player, player, rank, true);
    }

    private void LoadRanks() {
        try (InputStream inputStream = new FileInputStream(ranksPath)) {
            Yaml yaml = new Yaml(new CustomClassLoaderConstructor(Ranks.class.getClassLoader()));
            ranks = yaml.loadAs(inputStream, Ranks.class);
            if (ranks == null)
                ranks = new Ranks();
        }
        catch (IOException exception) {
            System.out.println("Rank file not found.");
        }
    }

    private void LoadPerms() {
        try (InputStream inputStream = new FileInputStream(permsPath)) {
            Yaml yaml = new Yaml();
            HashMap<String, String> perms = yaml.load(inputStream);
            if(perms == null)
                return;

            for (Map.Entry<String, String> entry : perms.entrySet()) {
                UUID key = UUID.fromString(entry.getKey());
                String value = entry.getValue();
                Rank rank = ranks.find(value, true);
                if (rank == null)
                    return;
                Permissions.put(key, rank);
            }
        }
        catch (IOException exception) {
            System.out.println("Permissions file not found.");
        }
    }

    public boolean SetPerm(Player sender, Player player, String strrank, boolean override) {
        Rank rank = ranks.find(sender,strrank);

        if(Objects.equals(strrank, DEFAULT_RANK) && override) {
            DeleteRank(player);
            return true;
        }

        if(sender != null && !GetRank(sender).hasPermission(sender,"r.setperm")) {
            ErrorExtension.Error(sender, "Insufficient permissions.");
            return false;
        }

        try (FileWriter writer = new FileWriter(permsPath, true)) {
            if(Permissions.get(player.getUniqueId()) != null) {
                if(!override)
                    return true;

                ModifyRank(player, rank);
                return true;
            }

            if(!override)
                writer.write(player.getUniqueId() + ": " + rank + "\n");
            Permissions.put(player.getUniqueId(), rank);
        }
        catch (IOException e) {
            System.out.println("Perms files not found. Will not be able to save permissions.");
            return false;
        }

        rank.SetAttachment(plugin, player);
        return true;
    }

    private void ModifyRank(Player sender, Player key, String value) { ModifyRank(key, ranks.find(sender,value)); }
    private void ModifyRank(Player key, Rank value) {
        GetRank(key).RemoveAttachment(key);
        value.SetAttachment(plugin, key);

        List<String> newLines = new ArrayList<>();
        Permissions.replace(key.getUniqueId(), value);
        try {
            Path path = Paths.get(permsPath);
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (line.contains(key.getUniqueId().toString()))
                    newLines.add(key.getUniqueId() + ": " + value);
                else
                    newLines.add(line);
            }

            Files.write(path, newLines, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("Perms files not found. Will not be able to modify permissions.");
        }
    }

    private void DeleteRank(Player key) {
        GetRank(key).RemoveAttachment(key);

        List<String> newLines = new ArrayList<>();
        Permissions.replace(key.getUniqueId(), ranks.find(DEFAULT_RANK));
        try {
            Path path = Paths.get(permsPath);
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (line.contains(key.getUniqueId().toString()))
                    continue;
                newLines.add(line);
            }

            Files.write(path, newLines, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("Perms files not found. Will not be able to modify permissions.");
        }
    }

    public Rank GetRank(Player player) { return GetRank(player.getUniqueId()); }
    public Rank GetRank(UUID id) { return Permissions.get(id); }
}
