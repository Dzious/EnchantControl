package com.dzious.bukkit.template.database;

import com.dzious.bukkit.template.Template;
import com.dzious.bukkit.template.utils.ConfigManager;

import java.net.URL;
import java.sql.DriverManager;

public class DataBaseCredentials {

    private Template plugin;
    private String type = "sqlite";
    private String tablePrefix = "Template_";
    private String tableName = null;
    private String host = "localhost";
    private int port = 3306;
    private String user = "root";
    private String password = "";
    private String dbName = "minecraft";

    public DataBaseCredentials(Template plugin, String tableName) {
        this.plugin = plugin;
        this.tableName = tableName;
        initFromConfig();

        plugin.getLogManager().logDebugConsole("Debug : Credential : "  + plugin.getDataFolder());
    }

    private void initFromConfig() {
        final ConfigManager config = plugin.getConfigManager();
        if (config.doPathExist("database.type"))
            type = config.getStringFromPath("database.type").toLowerCase();
        if (config.doPathExist("database.prefix"))
            tablePrefix = config.getStringFromPath("database.prefix");
        if (type.equals("mysql")) {
            if (config.doPathExist("database.mysql.host"))
                host = config.getStringFromPath("database.mysql.host");
            if (config.doPathExist("database.mysql.port"))
                port = config.getIntFromPath("database.mysql.port");
            if (config.doPathExist("database.mysql.dbName"))
                dbName = config.getStringFromPath("database.mysql.dbName");
            if (config.doPathExist("database.mysql.user"))
                user = config.getStringFromPath("database.mysql.user");
            if (config.doPathExist("database.mysql.password"))
                password = config.getStringFromPath("database.mysql.password");
        } else {
            type = "sqlite";
        }
    }

    public String toURL() {
        final StringBuilder sb = new StringBuilder();

        switch (type) {
            case "sqlite":
                sb.append("jdbc:sqlite:").append(plugin.getDataFolder().toString()).append("/data.db");
                break;
            case "mysql":
                sb.append("jdbc:mysql://").append(host).append(":").append(port).append("/").append(dbName);
                break;
            default:
                sb.append("jdbc:sqlite:").append(plugin.getDataFolder().toString()).append("/data.db");
                break;
        }
        return (new String(sb.toString()));
    }

    public String getType() {
        return (type);
    }

    public String getTablePrefix() {
        return (tablePrefix);
    }

    public String getTableName() {
        return (tableName);
    }

    public String getUser() {
        return (user);
    }

    public String getPassword() {
        return (password);
    }
}
