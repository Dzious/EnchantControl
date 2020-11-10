package com.dzious.bukkit.enchantcontrol.command;

import com.dzious.bukkit.enchantcontrol.EnchantControl;


public class CommandManager {

    private EnchantControl plugin;

    public CommandManager(EnchantControl plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        plugin.getLogManager().logInfo("CommandManager OnEnable");
        plugin.getCommand("EnchantControl").setExecutor(new CommandEnchantControl(plugin));
    }
}
