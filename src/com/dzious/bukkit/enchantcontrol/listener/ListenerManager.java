package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import com.dzious.bukkit.enchantcontrol.command.CommandEnchantControl;
import com.dzious.bukkit.enchantcontrol.command.CommandTemplate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class ListenerManager {
    private EnchantControl plugin;

    public ListenerManager(EnchantControl plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        plugin.getLogManager().logInfo("ListenerManager OnEnable");
        plugin.getServer().getPluginManager().registerEvents(new AnvilListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EnchantmentTableListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FishingListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LootGenerationListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new VillagerTradeListener(plugin), plugin);
    }

}