package com.dzious.bukkit.enchantcontrol.utils;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigManager {
    private FileConfiguration configFile = null;

    public ConfigManager (EnchantControl plugin) {
        plugin.saveDefaultConfig();
        configFile = plugin.getConfig();
    }

    public boolean doPathExist (String path) {
        return (configFile.contains(path));
    }

    public String getStringFromPath(String path) {
        return (configFile.getString(path));
    }

    public boolean getBooleanFromPath(String path) {
        return (configFile.getBoolean(path));
    }

    public int getIntFromPath(String path) {
        return (configFile.getInt(path));
    }

    public List<String> getListFromPath(String path) {
        return (configFile.getStringList(path));
    }


    public Map<String, Integer> loadEnchantments() {
        Map<String, Integer> map = new HashMap<>();
        Set<String> enchantments = configFile.getConfigurationSection("enchantments").getKeys(false);

        for (String enchantment : enchantments) {
            Integer level  = getIntFromPath("enchantments." + enchantment);
            map.put(enchantment, level);
        }
        return (map);
    }

    // ToDo Custom Config for language files + Getters for messages.
}
