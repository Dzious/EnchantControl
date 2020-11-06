package com.dzious.bukkit.enchantcontrol.utils;

import java.util.*;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;

public class EnchantmentManager {

    private  EnchantControl plugin;
    private final Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();


    public EnchantmentManager(EnchantControl plugin) {
        this.plugin = plugin;
        Map<String, Integer> configEnchantments = new HashMap<String, Integer>();

        plugin.getLogManager().logDebugConsole("Values : " + Enchantment.values().toString());

        plugin.getLogManager().logDebugConsole(enchantments.toString());


        if (this.plugin.getConfigManager().doPathExist("enchantments") == true) {
            configEnchantments = this.plugin.getConfigManager().loadEnchantments();
        }
        for (Enchantment enchantment : Enchantment.values()) {
            plugin.getLogManager().logDebugConsole("Currently processing : " + enchantment.getKey() + " it's level is " + enchantment.getMaxLevel());
            enchantments.put(enchantment, enchantment.getMaxLevel()); //replace(enchantment.getKey(), config.getValue());
            for (Map.Entry<String, Integer> config : configEnchantments.entrySet()) {
                if (config.getKey().equals(enchantment.getKey().toString().split(":")[1]) == true) {
                    plugin.getLogManager().logDebugConsole("Replaced " + enchantment.getKey() + ". Old value was " + enchantment.getMaxLevel() + ", new value is " + config.getValue() + ".");
                    enchantments.replace(enchantment, config.getValue());
                    break;
                }
            }
        }
        plugin.getLogManager().logDebugConsole(enchantments.toString());
    }

    public Map<Enchantment, Integer> getAffectedEnchantments()
    {
        return (enchantments);
    }

    public boolean hasEnchantActive(ItemStack item) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(item)) {
                if (enchantments.containsKey(enchantment) == false || enchantments.get(enchantment) > 0) {
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

    public Enchantment rerollEnchantment(ItemStack item, List<Enchantment> currentEnchantments) {

        List<Enchantment> validEnchantmentsList = getValidEnchantments(item);
        List<Enchantment> currentValidEnchantmentsList = new ArrayList<>();
        if (currentEnchantments != null)
            currentValidEnchantmentsList = getValidEnchantments(item, currentEnchantments);

        if (validEnchantmentsList.size() == currentValidEnchantmentsList.size())
            return (null);

        Enchantment newEnchantment = validEnchantmentsList.get((int) (Math.random() % validEnchantmentsList.size()));

        while (currentValidEnchantmentsList.contains(newEnchantment))
            newEnchantment = validEnchantmentsList.get((int) (Math.random() % validEnchantmentsList.size()));
        return (newEnchantment);
    }
}
