package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnvilListener implements Listener {
    private final EnchantControl plugin;
    boolean bypassMinecraftMaxLevel = false;

    public AnvilListener (EnchantControl plugin) {
        this.plugin = plugin;
        if (plugin.getConfigManager().doPathExist("bypass_levels"))
            bypassMinecraftMaxLevel = plugin.getConfigManager().getBooleanFromPath("bypass_levels");
    }


    // TODO Manage equipment upgrade over minecraft vanilla limit
    @EventHandler
    public void onFusionPreparation(PrepareAnvilEvent e) {

        if (e.getResult() == null || (e.getResult().getEnchantments().isEmpty() == true && (e.getResult().getItemMeta() == null || (e.getResult().getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().isEmpty() == true))) {
            return;
        }

        plugin.getLogManager().logDebugConsole("Type : " +  e.getResult().getType());

        if (e.getResult().getEnchantments().isEmpty() == false) {
            plugin.getLogManager().logDebugConsole("Enchantments : " +  e.getResult().getEnchantments());
            for (Map.Entry<Enchantment, Integer> enchantment : e.getResult().getEnchantments().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    plugin.getLogManager().logDebugConsole("Enchantments : Remove");
                    e.getResult().removeEnchantment(enchantment.getKey());
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    plugin.getLogManager().logDebugConsole("Enchantments : Replace");
                    e.getResult().removeEnchantment(enchantment.getKey());
                    e.getResult().addEnchantment(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())); // TODO may have to reset item (e.setResult(ItemStack))
                }
            }
        } else {
            plugin.getLogManager().logDebugConsole("Stored Enchantments : " +  ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants());
            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    plugin.getLogManager().logDebugConsole("Stored Enchantments : Remove");
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)e.getResult().getItemMeta());
                    meta.removeStoredEnchant(enchantment.getKey());
                    e.getResult().setItemMeta(meta);
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    plugin.getLogManager().logDebugConsole("Stored Enchantments : Replace");
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)e.getResult().getItemMeta());
                    meta.removeStoredEnchant(enchantment.getKey());
                    meta.addStoredEnchant(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), true); // TODO may have to reset item (e.setResult(ItemStack))
                    e.getResult().setItemMeta(meta);
                }
            }
            if (e.getResult().getType() == Material.ENCHANTED_BOOK && ((EnchantmentStorageMeta)(e.getResult().getItemMeta())).getStoredEnchants().isEmpty() == true) {
                e.setResult(new ItemStack(Material.BOOK));
            } else {
                plugin.getLogManager().logDebugConsole("Stored Enchantments : " +  ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants());
            }
        }
        
        applyOverFusion(e);
    }

    private void applyOverFusion(PrepareAnvilEvent e) {
        if (bypassMinecraftMaxLevel == true && hasPossibleOverFusion(e.getInventory().getContents())) {
            for (Map.Entry<Enchantment, Integer> enchantment : getFusionEnchantments(e.getInventory().getContents()).entrySet()) {
                if (e.getResult().getEnchantments().isEmpty() == false) {
                    e.getResult().removeEnchantment(enchantment.getKey());
                    e.getResult().addEnchantment(enchantment.getKey(), enchantment.getValue());
                } else { 
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)e.getResult().getItemMeta());
                    meta.removeStoredEnchant(enchantment.getKey());
                    meta.addStoredEnchant(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), true);
                    e.getResult().setItemMeta(meta);
                }
            }
        }
        
    }

    boolean hasPossibleOverFusion(ItemStack[] items) {
        List<Map<Enchantment,Integer>> itemsEnchants = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            if (items[i] == null || (items[i].getEnchantments().isEmpty() == true && 
                (items[i].getItemMeta() == null || 
                (items[i].getItemMeta() instanceof EnchantmentStorageMeta) == false ||
                ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().isEmpty() == true))) {
                return (false);
            }
            if (items[i].getEnchantments().isEmpty() == false) {
                itemsEnchants.add(items[i].getEnchantments());
            } else {
                itemsEnchants.add(((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants());
            }

        }
        Map<Enchantment, Integer> rhsEnchantments = itemsEnchants.get(1);
        for (Map.Entry<Enchantment, Integer> lhsEnchantments : itemsEnchants.get(0).entrySet()) {
            if (rhsEnchantments.containsKey(lhsEnchantments.getKey()) &&
                lhsEnchantments.getValue() == rhsEnchantments.get(lhsEnchantments.getKey()) &&
                lhsEnchantments.getValue() >= lhsEnchantments.getKey().getMaxLevel() &&
                lhsEnchantments.getValue() < plugin.getEnchantmentManager().getAffectedEnchantments().get(lhsEnchantments.getKey()) ) {
                return (true);
            }
        }
        return (false);
    }

    Map<Enchantment, Integer> getFusionEnchantments(ItemStack[] items) {
        List<Map<Enchantment,Integer>> itemsEnchants = new ArrayList<>();
        Map<Enchantment,Integer> fusionEnchantments = new HashMap<>();


        for (int i = 0; i < 2; i++) {
            if (items[i] == null || (items[i].getEnchantments().isEmpty() == true && (items[i].getItemMeta() == null || (items[i].getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().isEmpty() == true))) {
                return (null);
            }
            if (items[i].getEnchantments().isEmpty() == false) {
                itemsEnchants.add(items[i].getEnchantments());
            } else {
                itemsEnchants.add(((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants());
            }

        }
        Map<Enchantment, Integer> rhsEnchantments = itemsEnchants.get(1);
        for (Map.Entry<Enchantment, Integer> lhsEnchantments : itemsEnchants.get(0).entrySet()) {
            if (rhsEnchantments.containsKey(lhsEnchantments.getKey()) && lhsEnchantments.getValue() == rhsEnchantments.get(lhsEnchantments.getKey()) && lhsEnchantments.getValue() == lhsEnchantments.getKey().getMaxLevel()) {
                fusionEnchantments.put(lhsEnchantments.getKey(), lhsEnchantments.getValue());
            }
        }
        return (fusionEnchantments);
    }
}
