package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.lang.Boolean;

public class ListenerManager {
    private final EnchantControl plugin;
    private Map<String, Boolean> configurableListenersValues = new HashMap<>();    

    public ListenerManager(EnchantControl plugin) {
        this.plugin = plugin;
        initMap();
        for (Map.Entry<String, Boolean> listener : configurableListenersValues.entrySet())
            if (plugin.getConfigManager().doPathExist(listener.getKey()) == true)
                configurableListenersValues.replace(listener.getKey(), plugin.getConfigManager().getBooleanFromPath(listener.getKey()));
    }

    private void initMap()
    {
        configurableListenersValues.put("mending_repair", true);
        configurableListenersValues.put("update_inventory", false);
    }

    public void onEnable() {
        plugin.getLogManager().logInfo("ListenerManager OnEnable");
        plugin.getServer().getPluginManager().registerEvents(new AnvilListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EnchantmentTableListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FishingListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LootGenerationListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerEnchantmentListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new VillagerTradeListener(plugin), plugin);

        if (plugin.getEnchantmentManager().getAffectedEnchantments().get(Enchantment.MENDING) < 1 && configurableListenersValues.get("mending_repair") == false)
            plugin.getServer().getPluginManager().registerEvents(new MendingCancelerListener(), plugin);
        
       if (configurableListenersValues.get("update_inventory") == true)
           plugin.getServer().getPluginManager().registerEvents(new InventoryListener(plugin), plugin); 

//        if (doMendingRepair == false)
//            plugin.getServer().getPluginManager().registerEvents(new MendingCancelerListener(), plugin);
    }

}