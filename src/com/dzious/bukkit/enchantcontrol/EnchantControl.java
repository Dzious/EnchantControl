package com.dzious.bukkit.enchantcontrol;

import com.dzious.bukkit.enchantcontrol.command.CommandManager;
import com.dzious.bukkit.enchantcontrol.listener.ListenerManager;
import com.dzious.bukkit.enchantcontrol.plugin.PlayersManager;
import com.dzious.bukkit.enchantcontrol.utils.ConfigManager;
import com.dzious.bukkit.enchantcontrol.plugin.EnchantmentManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.dzious.bukkit.enchantcontrol.utils.LogManager;

import java.util.logging.Logger;

public class EnchantControl extends JavaPlugin {

    private static EnchantControl INSTANCE;
    private LogManager logManager;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private EnchantmentManager enchantmentManager;
    private ListenerManager listenerManager;
    private PlayersManager playersManager;


    @Override
    public void onLoad() {
        return;
    }

    @Override
    public void onEnable() {
        Logger logger = Logger.getLogger("Minecraft");
        logger.info("Enchant Control starting...");
        INSTANCE = this;
        configManager = new ConfigManager(this);
        logManager = new LogManager(INSTANCE, logger, configManager);
        enchantmentManager = new EnchantmentManager(this);
        commandManager = new CommandManager(INSTANCE);
        commandManager.onEnable();
        listenerManager = new ListenerManager(INSTANCE);
        listenerManager.onEnable();
        playersManager = new PlayersManager();
        logManager.logInfo("Enchant Control started !");
        return;
    }

    @Override
    public void onDisable() {
        logManager.logInfo("Enchant Control stopping...");
        logManager.logInfo("Enchant Control stopped !");
        return;
    }

    public void disablePlugin() {
        logManager.logSevere("This is a fatal error, disabling Enchant Control");
        setEnabled(false);
    }

    public LogManager getLogManager() {
        return (logManager);
    }

    public ConfigManager getConfigManager() {
        return (configManager);
    }

    public EnchantmentManager getEnchantmentManager() {return (enchantmentManager); }

    public PlayersManager getPlayersManager() {return (playersManager);}

}
