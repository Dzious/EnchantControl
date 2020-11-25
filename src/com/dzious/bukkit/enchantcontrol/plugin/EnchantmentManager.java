package com.dzious.bukkit.enchantcontrol.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantmentManager {

    private  EnchantControl plugin;
    private final Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();


    public EnchantmentManager(EnchantControl plugin) {
        this.plugin = plugin;
        registerEnchantments("minecraft", Enchantment.values());
    }


    public void registerEnchantments(@NotNull String path, @NotNull Enchantment[] enchantmentList)
    {
        Map<String, Integer> configEnchantments = new HashMap<String, Integer>();

        if (this.plugin.getConfigManager().doPathExist("enchantments." + path) == true) {
            configEnchantments = this.plugin.getConfigManager().loadEnchantments(path);
        }
        for (Enchantment enchantment : enchantmentList) {
            plugin.getLogManager().logDebugConsole("Currently processing : " + ChatColor.GREEN + enchantment.getKey() + ChatColor.WHITE + ". It's level is " + ChatColor.RED + enchantment.getMaxLevel());
            enchantments.put(enchantment, enchantment.getMaxLevel());
            for (Map.Entry<String, Integer> config : configEnchantments.entrySet()) {
                if (enchantment.getKey().toString().equals(path + ":" + config.getKey()) == true) {
                    plugin.getLogManager().logDebugConsole("Replaced " + enchantment.getKey() + ". Old value was " + enchantment.getMaxLevel() + ", new value is " + config.getValue() + ".");
                    enchantments.replace(enchantment, config.getValue());
                    break;
                }
            }
        }
        plugin.getLogManager().logDebugConsole(enchantments.toString());
    }

    public void reload()
    {
        plugin.getLogManager().logDebugConsole("Reloading enchantments");
        
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            plugin.getLogManager().logDebugConsole("Currently processing : " + ChatColor.GREEN + enchantment.getKey() + ChatColor.WHITE + ". It's level is " + ChatColor.RED + enchantment.getValue());
            String[] enchantmentId = enchantment.getKey().getKey().toString().split(":");
            if (plugin.getConfigManager().doPathExist("enchantments." + enchantmentId[0] + "." + enchantmentId[1])) {
                int newLevel = plugin.getConfigManager().getIntFromPath("enchantments." + enchantmentId[0] + "." + enchantmentId[1]);
                if (enchantment.getValue() != newLevel) {
                    plugin.getLogManager().logDebugConsole("Replaced " + enchantment.getKey() + ". Old value was " + enchantment.getValue() + ", new value is " + newLevel + ".");
                    enchantment.setValue(newLevel);
                }
            }
        }

        plugin.getLogManager().logDebugConsole("Enchantments Reloaded");
    }


    public Map<Enchantment, Integer> getAffectedEnchantments()
    {
        return (enchantments);
    }

    public boolean hasEnchantActive(ItemStack item) {
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (enchantment.getKey().canEnchantItem(item)) {
                if (enchantments.containsKey(enchantment.getKey()) == false || enchantments.get(enchantment.getKey()) > 0) {
                    return (true);
                }
            }
        }
        return (false);
    }

    public boolean isValidEnchant(Enchantment enchantment) {
       if (enchantments.containsKey(enchantment) == false || enchantments.get(enchantment) > 0) {
           return (true);
       }
       return (false);
    }

    public List<Enchantment> getValidEnchantments(ItemStack item) {
        List<Enchantment> validEnchantments = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (enchantment.getKey().canEnchantItem(item) == true && enchantment.getValue() > 0)
                validEnchantments.add(enchantment.getKey());
        }
        return (validEnchantments);
    }

    public List<Enchantment> getValidEnchantments(ItemStack item, List<Enchantment> currents) {
        List<Enchantment> validEnchantmentsList = getValidEnchantments(item);
        List<Enchantment> validEnchantments = new ArrayList<>();

        for (Enchantment enchantment : currents) {
            if (validEnchantmentsList.contains(enchantment))
                validEnchantments.add(enchantment);
        }
        return (validEnchantments);
    }

    public List<Enchantment> getValidEnchantments(List<Enchantment> validEnchantments, List<Enchantment> currents) {
        List<Enchantment> newValidEnchantments = new ArrayList<>();
        boolean conflict = false;

        for (Enchantment enchantment : validEnchantments) {
            conflict = false;
            for (Enchantment current : currents) {
                if (current.conflictsWith(enchantment)) {
                    conflict = true;
                    break;
                }
            }
            if (conflict == false) {
                newValidEnchantments.add(enchantment);
            }
        }
        return (validEnchantments);
    }


    public Enchantment rerollEnchantment(ItemStack item, List<Enchantment> currentEnchantments) {

        List<Enchantment> validEnchantmentsList = getValidEnchantments(item);
        List<Enchantment> currentValidEnchantmentsList = new ArrayList<>();

        if (currentEnchantments != null) {
            currentValidEnchantmentsList = getValidEnchantments(item, currentEnchantments);
            validEnchantmentsList = getValidEnchantments(validEnchantmentsList, currentEnchantments);
        }

        if (validEnchantmentsList.size() == currentValidEnchantmentsList.size())
            return (null);

        int idx = (int) (Math.random() % validEnchantmentsList.size());
        Enchantment newEnchantment = validEnchantmentsList.get(idx);
        while (currentValidEnchantmentsList.contains(newEnchantment)) {
            newEnchantment = validEnchantmentsList.get(idx);
            idx = idx + 1;
            if (idx == validEnchantmentsList.size()) {
                idx = 0;
            }
        }
        return (newEnchantment);
    }
}
