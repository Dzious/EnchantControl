package com.dzious.bukkit.template.command;

import com.dzious.bukkit.template.Template;
import com.dzious.bukkit.template.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChat implements CommandExecutor {

    private Template plugin;
    private final String commandName = "Chat";

    public CommandChat(Template plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player == false) {
            plugin.getLogManager().logWarning(sender.getClass().toString() + " cannot use this command.");
            return (true);
        }
        for (String commandLabel : plugin.getCommand(commandName).getAliases())
            if (label.toLowerCase().equals(commandLabel.toLowerCase()) == true && sender.hasPermission(plugin.getCommand(commandName).getPermission()) == true && args[0] != null)
                switch (args[0]) {
                    case "info":
                        sender.sendMessage(plugin.getCommand(commandName).getUsage());
                        return (true);
                    case "join":
                        if (args[1] == null)
                            sender.sendMessage(ChatColor.RED + "Invalid argument for command " + ChatColor.BLUE + Utils.concatCommand(label, args));
                        return (plugin.getChannelManager().joinChannel((Player)sender, args[1]));

                }
        return (false);
    }
}
