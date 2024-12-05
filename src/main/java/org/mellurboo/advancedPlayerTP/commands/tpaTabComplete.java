package org.mellurboo.advancedPlayerTP.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class tpaTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("accept");
            completions.add("deny");
            completions.add("cancel");

            if (sender.hasPermission("ReallyTPA.admin") || sender.isOp()) {
                completions.add("clear");
                completions.add("reload");
                completions.add("toggle");
            }

        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        // Filter the list to only include what matches the current input
        List<String> result = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                result.add(completion);
            }
        }

        return result;
    }
}
