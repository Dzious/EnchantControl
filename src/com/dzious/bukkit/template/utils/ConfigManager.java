package com.dzious.bukkit.template.utils;

import com.dzious.bukkit.template.Template;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    private FileConfiguration configFile = null;

    public ConfigManager (Template plugin) {
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

    // ToDo Custom Config for language files + Getters for messages.
}
