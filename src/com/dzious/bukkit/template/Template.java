package com.dzious.bukkit.template;

import com.dzious.bukkit.template.chat.ChannelManager;
import com.dzious.bukkit.template.chat.ChatManager;
import com.dzious.bukkit.template.command.CommandManager;
import com.dzious.bukkit.template.command.CommandTemplate;
import com.dzious.bukkit.template.database.DataBaseManager;
import com.dzious.bukkit.template.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.dzious.bukkit.template.utils.LogManager;

import java.util.logging.Logger;

public class Template extends JavaPlugin {

    private static Template INSTANCE;
    private LogManager logManager;
    private DataBaseManager databaseManager;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private ChannelManager channelManager;
    private ChatManager chatManager;

    @Override
    public void onLoad() {
        return;
    }

    @Override
    public void onEnable() {
        Logger logger = Logger.getLogger("Minecraft");
        logger.info("RolePlay Engine starting...");
        INSTANCE = this;
        configManager = new ConfigManager(this);
        logManager = new LogManager(INSTANCE, logger, configManager);
        databaseManager = new DataBaseManager(INSTANCE);
        databaseManager.onEnable();
        commandManager = new CommandManager(INSTANCE);
        commandManager.onEnable();
        channelManager = new ChannelManager(INSTANCE);
        chatManager = new ChatManager(INSTANCE);
        logManager.logInfo("RolePlay Engine started !");
        return;
    }

    @Override
    public void onDisable() {
        logManager.logInfo("RolePlay Engine stopping...");
        if (databaseManager != null)
        databaseManager.onDisable();
        logManager.logInfo("RolePlay Engine stopped !");
        return;
    }

    public void disablePlugin() {
        logManager.logSevere("This is a fatal error, disabling RolePlay Engine");
        setEnabled(false);
    }

    public LogManager getLogManager() {
        return (logManager);
    }

    public ConfigManager getConfigManager() {
        return (configManager);
    }

    public DataBaseManager getDatabaseManager() { return (databaseManager); }

    public ChannelManager getChannelManager() {
        return (channelManager);
    }
}
