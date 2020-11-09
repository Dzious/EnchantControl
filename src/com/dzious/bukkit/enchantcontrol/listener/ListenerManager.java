package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import com.dzious.bukkit.enchantcontrol.command.CommandEnchantControl;
import com.dzious.bukkit.enchantcontrol.command.CommandTemplate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class ListenerManager {
    private final EnchantControl plugin;
    private boolean doMendingRepair = true;

    public ListenerManager(EnchantControl plugin) {
        this.plugin = plugin;
        if (plugin.getConfigManager().doPathExist("mending_repair"))
            doMendingRepair = plugin.getConfigManager().getBooleanFromPath("mending_repair");
    }

    public void onEnable() {
        plugin.getLogManager().logInfo("ListenerManager OnEnable");
        plugin.getServer().getPluginManager().registerEvents(new AnvilListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EnchantmentTableListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FishingListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LootGenerationListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerEnchantmentListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new VillagerTradeListener(plugin), plugin);

        if (plugin.getEnchantmentManager().getAffectedEnchantments().get(Enchantment.MENDING) < 1 && doMendingRepair == false)
            plugin.getServer().getPluginManager().registerEvents(new MendingCancelerListener(), plugin);

//        if (doMendingRepair == false)
//            plugin.getServer().getPluginManager().registerEvents(new MendingCancelerListener(), plugin);
    }

}