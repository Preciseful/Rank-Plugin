package developful.plugin.commands.rank;

import developful.plugin.extensions.ErrorExtension;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ranks {
    public List<Rank> ranks = new ArrayList<>();

    public Rank find(String name) { return this.find(name, true); }
    public Rank find(String name, boolean error) {
        List<Rank> ranks = this.ranks.stream()
                .filter(p -> Objects.equals(p.name, name))
                .toList();
        if(ranks.size() > 1 && error)
            throw new RuntimeException("Multiple ranks with the same name found. Name: " + name + ".");
        if(ranks.isEmpty() && error)
            throw new RuntimeException("No ranks with such name found. Name: " + name + ".");

        return ranks.get(0);
    }

    public Rank find(Player sender, String name) { return this.find(sender, name, true); }
    public Rank find(Player sender, String name, boolean error) {
        if(sender == null)
            return find(name, error);

        List<Rank> ranks = this.ranks.stream()
                .filter(p -> Objects.equals(p.name, name))
                .toList();
        if(ranks.size() > 1 && error) {
            ErrorExtension.Error(sender, "Multiple ranks with the same name found. Name: " + name + ".");
            return null;
        }
        if(ranks.isEmpty() && error) {
            ErrorExtension.Error(sender, "No ranks with such name found. Name: " + name + ".");
            return null;
        }

        return ranks.get(0);
    }
}
