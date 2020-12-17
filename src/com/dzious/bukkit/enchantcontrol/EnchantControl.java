package com.dzious.bukkit.enchantcontrol;

import java.util.logging.Logger;

import com.dzious.bukkit.enchantcontrol.command.CommandManager;
import com.dzious.bukkit.enchantcontrol.listener.ListenerManager;
import com.dzious.bukkit.enchantcontrol.plugin.EnchantmentManager;
import com.dzious.bukkit.enchantcontrol.plugin.PlayersManager;
import com.dzious.bukkit.enchantcontrol.utils.ConfigManager;
import com.dzious.bukkit.enchantcontrol.utils.LogManager;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantControl extends JavaPlugin {

    private static EnchantControl INSTANCE;
    private LogManager logManager;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private EnchantmentManager enchantmentManager;
    private ListenerManager listenerManager;
    private PlayersManager playersManager;
    private NamespacedKey key;

    public enum TagType {
        EVENT,
    };


    @Override
    public void onLoad() {
        return;
    }

    @Override
    public void onEnable() {
        Logger logger = Logger.getLogger("Minecraft");
        logger.info("Enchant Control starting...");
        INSTANCE = this;
        key = new NamespacedKey(INSTANCE, "ENCHANT_CONTROL");
        configManager = new ConfigManager(INSTANCE);
        logManager = new LogManager(INSTANCE, logger, configManager);
        enchantmentManager = new EnchantmentManager(INSTANCE);
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

    public EnchantmentManager getEnchantmentManager() {
        return (enchantmentManager);
    }

    public PlayersManager getPlayersManager() {
        return (playersManager);
    }

    public NamespacedKey getNamespacedKey() {
        return (key);
    }

}
