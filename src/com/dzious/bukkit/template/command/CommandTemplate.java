package com.dzious.bukkit.template.command;

import com.dzious.bukkit.template.Template;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandTemplate implements CommandExecutor {

    private Template plugin;
    private final String commandName = "Template";

    public CommandTemplate(Template plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String commandLabel : plugin.getCommand(commandName).getAliases())
            if (label.toLowerCase().equals(commandLabel.toLowerCase()) == true) {// && (sender instanceof ConsoleCommandSender || sender.hasPermission(plugin.getCommand(commandName).getPermission()))) {
                plugin.getLogManager().logInfo("sender : " + sender.getClass().toString());
                plugin.getLogManager().logInfo("command : " + command.toString());
                plugin.getLogManager().logInfo("label : " + label);
                for (int i = 0; i < args.length; i++)
                    plugin.getLogManager().logInfo("arg[" + i + "] : " + args[i]);
                return (true);
            }
        return (false);
    }
}
