package com.dzious.bukkit.enchantcontrol.command;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEnchantControl implements CommandExecutor {

    private EnchantControl plugin;
    private final String commandName = "EnchantControl";

    public CommandEnchantControl(EnchantControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player == false)
            return (false);
        for (String commandLabel : plugin.getCommand(commandName).getAliases())
            if (label.toLowerCase().equals(commandLabel.toLowerCase()) == true) {// && (sender instanceof ConsoleCommandSender || sender.hasPermission(plugin.getCommand(commandName).getPermission()))) {
                plugin.getEnchantmentManager().removeEnchantment(((Player) sender).getInventory().getItemInMainHand());
            }
        return (false);
    }
}