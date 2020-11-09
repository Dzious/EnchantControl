package com.dzious.bukkit.enchantcontrol.utils;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class LogManager {

    private EnchantControl plugin;
    private Logger logger;
    private boolean debugEnable = false;
    private String debugPlayer = null;

    public LogManager(EnchantControl plugin, Logger logger, ConfigManager configManager) {
        this.plugin = plugin;
        this.logger = logger;
        if (configManager.doPathExist("debug.enable"))
            this.debugEnable = configManager.getBooleanFromPath("debug.enable");
        if (configManager.doPathExist("debug.player"))
            this.debugPlayer = configManager.getStringFromPath("debug.player");
    }

    public void logInfo(String msg) {
        logger.info(msg);
    }

    public void logWarning(String msg) {
        logger.warning(msg);
    }

    public void logSevere(String msg) {
        logger.severe(msg);
    }

    public void logDebugConsole(String msg) {
        if (isDebugEnable() == true)
            logger.info(ChatColor.BLUE + "[" + plugin.getName() + "] Debug : " + ChatColor.WHITE + msg);
    }

    public void logDebugPlayer(String msg) {
        if (isDebugEnable() == true && getDebugPlayer() != null)
            for (Player p : plugin.getServer().getOnlinePlayers())
                if (p.getName() == getDebugPlayer())
                    p.sendMessage("[" + plugin.getName() + "] Debug : " + msg);
    }

    public void logException (Exception e) {
        if (isDebugEnable() == true)
            e.printStackTrace();
    }

    private boolean isDebugEnable() {
        return (debugEnable);
    }

    private String getDebugPlayer() {
        return (debugPlayer);
    }
}
