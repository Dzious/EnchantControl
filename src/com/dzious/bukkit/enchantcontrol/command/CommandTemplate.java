package com.dzious.bukkit.enchantcontrol.command;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTemplate implements CommandExecutor {


    private EnchantControl plugin;
    private final String commandName = "Template";


    public CommandTemplate(EnchantControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String commandLabel : plugin.getCommand(commandName).getAliases())
        {
            for (Material material : Material.values()) {
                if (sender instanceof Player)
                    plugin.getLogManager().logDebugPlayer(material.getKey().toString());
                plugin.getLogManager().logDebugConsole(material.getKey().toString());
            }
            
            plugin.getLogManager().logDebugConsole("alias : " + commandLabel);
            if (label.toLowerCase().equals(command.getLabel().toLowerCase()) == true || label.toLowerCase().equals(commandLabel.toLowerCase()) == true) {// && (sender instanceof ConsoleCommandSender || sender.hasPermission(plugin.getCommand(commandName).getPermission()))) {
                    plugin.getLogManager().logInfo("sender : " + sender.getClass().toString());
                    plugin.getLogManager().logInfo("command : " + command.toString());
                    plugin.getLogManager().logInfo("command name : " + command.getName().toString());
                    plugin.getLogManager().logInfo("command label : " + command.getLabel().toString());
                    plugin.getLogManager().logInfo("label : " + label);
                    for (int i = 0; i < args.length; i++)
                        plugin.getLogManager().logInfo("arg[" + i + "] : " + args[i]);
                    return (true);
                }
        }
        return (false);
    }
}