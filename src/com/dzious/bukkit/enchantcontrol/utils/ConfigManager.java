package com.dzious.bukkit.enchantcontrol.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private FileConfiguration configFile = null;
    private final EnchantControl plugin;

    public ConfigManager(EnchantControl plugin) {
        this.plugin = plugin;
        File file = new File(this.plugin.getDataFolder(), "config.yml");
        if (file.exists() == false)
            this.plugin.saveDefaultConfig();
        configFile = this.plugin.getConfig();
    }

    public void reload() throws FileNotFoundException, IOException, InvalidConfigurationException {
        File file = new File(plugin.getDataFolder(), "config.yml");
        configFile.load(file);
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


    public Map<String, Integer> loadEnchantments(String path) {
        Map<String, Integer> map = new HashMap<>();
        Set<String> enchantments = configFile.getConfigurationSection("enchantments." + path).getKeys(false);

        for (String enchantment : enchantments) {
            Integer level  = getIntFromPath("enchantments." + path + "." + enchantment);
            map.put(enchantment, level);
        }
        return (map);
    }

    // ToDo Custom Config for language files + Getters for messages.
}
