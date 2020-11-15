package com.dzious.bukkit.enchantcontrol.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import com.dzious.bukkit.enchantcontrol.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;

public class CommandEnchantControl implements CommandExecutor, TabCompleter {

    private EnchantControl plugin;
    private final String commandName = "EnchantControl";

    private Map<String, List<String>> tabComplete = new HashMap<>();

    List<String> enchantcontrolTabComplete = Arrays.asList("reload");

    public CommandEnchantControl(EnchantControl plugin) {
        this.plugin = plugin;
        initTabComplete();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getLogManager().logDebugConsole("Enchant Control Command");
        plugin.getLogManager().logDebugConsole("length : " + args.length);
        for (int i = 0; i < args.length; i++)
            plugin.getLogManager().logDebugConsole("arg[" + i + "] : " + args[i]);

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            try {
                plugin.getConfigManager().reload();
                plugin.getEnchantmentManager().reload();
            } catch (IOException | InvalidConfigurationException e) {
                plugin.getLogManager().logSevere("Config file does not exists. Reload failed.");
                e.printStackTrace();
            }
            return (true);
        }
        return (false);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0)
            return (tabComplete.get(commandName));
        else
            return (tabComplete.get(Utils.concatCommand(commandName, args, ".")));
    }

    private void initTabComplete() {
        tabComplete.put(commandName, createTabCompleteList("enchantcontrol", enchantcontrolTabComplete));
        tabComplete.put(commandName + ".reload", null);
    }

    
    private List<String> createTabCompleteList(String command, List<String> defaultTabComplete) {
        List<String> tabComplete = new ArrayList<>();

        for (String str : defaultTabComplete) {
            if (plugin.getConfigManager().doPathExist(enchantcontrolTabComplete + "." + str + ".command")) {
                tabComplete.add(plugin.getConfigManager().getStringFromPath(enchantcontrolTabComplete + "." + str + ".command"));
            } else {
                tabComplete.add(str);
            }
        }
        return (null);
    }

}