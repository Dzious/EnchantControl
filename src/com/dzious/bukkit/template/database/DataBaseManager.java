package com.dzious.bukkit.template.database;

import com.dzious.bukkit.template.Template;

public class DataBaseManager {
    private Template plugin;
    private DataBaseConnection playersData;

    public DataBaseManager (Template plugin) {
        this.plugin = plugin;
        playersData = new DataBaseConnection(this.plugin, new DataBaseCredentials(this.plugin, "PlayerData"));
    }

    public void onEnable() {
        playersData.onEnable();
    }

    public void onDisable() {
            playersData.onDisable();
    }

}
