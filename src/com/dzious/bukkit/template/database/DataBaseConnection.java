package com.dzious.bukkit.template.database;

import com.dzious.bukkit.template.Template;
import org.bukkit.Bukkit;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.UUID;

public class DataBaseConnection {

    private Template plugin;
    private DataBaseCredentials dbCredential;
    private Connection connection = null;

    public DataBaseConnection (Template plugin, DataBaseCredentials dbCredential) {
        this.plugin = plugin;
        this.dbCredential = dbCredential;
    }

    public void onEnable() {
        connect();
        if (connection != null)
            plugin.getLogManager().logDebugConsole("initDatabase()");
            initDatabase();
    }

    public void onDisable() {
        disconnect();
    }

    public void setField(UUID player, String field, String data) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                try {
                    PreparedStatement statement = connection.prepareStatement("UPDATE `" + dbCredential.getTablePrefix() + dbCredential.getTableName() + "` SET `" + field + "`=" + data  + " WHERE `uuid`=" + player.toString() + ";");
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    plugin.getLogManager().logException(e);
                }
            }
        });
    }

    public void setField(UUID player, String field, int data) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                try {
                    PreparedStatement statement = connection.prepareStatement("UPDATE `" + dbCredential.getTablePrefix() + dbCredential.getTableName() + "` SET `" + field + "`=" + data + " WHERE `uuid`=" + player.toString() + ";");
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    plugin.getLogManager().logException(e);
                }
            }
        });
    }

    public String getField(UUID player, String field) {
        final ResultSet[] result = {null};
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                connect();
                try {
                    PreparedStatement statement = connection.prepareStatement("Select `" + field + "` FROM `" + dbCredential.getTablePrefix() + dbCredential.getTableName() + "WHERE `uuid`=" + player.toString() + ";");
                    result[0] = statement.executeQuery();
                } catch (Exception e) {
                    plugin.getLogManager().logException(e);
                }
            }
        });
        try {
            if (result[0] != null && result[0].next() == true)
                return (result[0].getString(field));
            else
                return (null);
        } catch (SQLException e) {
            plugin.getLogManager().logException(e);
            return (null);
        }
    }

    private void connect() {
        plugin.getLogManager().logInfo("Connecting to " + dbCredential.getType() + " database...");
        try {
            switch (dbCredential.getType()) {
                case "sqlite":
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection(dbCredential.toURL());
                    break;
                case "mysql":
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(dbCredential.toURL(), dbCredential.getUser(), dbCredential.getPassword());
                    break;
                default:
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection(dbCredential.toURL());
                    break;
            }
            plugin.getLogManager().logInfo("Connection with database established.");
        } catch (Exception e) {
            plugin.getLogManager().logSevere("Connection with database failed.");
            plugin.getLogManager().logDebugConsole("URL : " + dbCredential.toURL());
            plugin.getLogManager().logException(e);
            plugin.disablePlugin();
        }
    }

    private void initDatabase() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + dbCredential.getTablePrefix() + dbCredential.getTableName()  + "` (`uuid` varchar(36) NOT NULL UNIQUE, `TemplateString` varchar(100), `TemplateInt` INT, `TemplateText` TEXT) ;");
                    statement.execute();
                    statement.close();
                } catch (SQLException e) {
                    plugin.getLogManager().logException(e);
                }
            }
        });
    }

    private void disconnect() {
        if (connection != null) {
            try {
                if (connection.isClosed() == false) {
                    connection.close();
                }
            } catch (SQLException e) {
                plugin.getLogManager().logSevere("Connection with database failed.");
                plugin.getLogManager().logException(e);
            }
        }
    }
}