package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class AnvilListener implements Listener {
    private final EnchantControl plugin;
    boolean bypassMinecraftMaxLevel = false;

    public AnvilListener (EnchantControl plugin) {
        this.plugin = plugin;
        if (plugin.getConfigManager().doPathExist("bypass_levels"))
            bypassMinecraftMaxLevel = plugin.getConfigManager().getBooleanFromPath("bypass_levels");
    }

    @EventHandler
    public void onFusionPreparation(PrepareAnvilEvent e) {
    
        if (e.getResult() == null || e.getResult().getItemMeta() == null || 
            (e.getResult().getEnchantments().isEmpty() &&
            (!(e.getResult().getItemMeta() instanceof EnchantmentStorageMeta) ||
            ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().isEmpty()))) {
            return;
        }

        plugin.getLogManager().logDebugConsole("Item type : " +  e.getResult().getType());
        
        Map<Enchantment, Integer> enchantments;
        if (bypassMinecraftMaxLevel) {
            enchantments = computeFusion(e.getInventory().getContents());
        } else if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
            enchantments = new HashMap<>(e.getResult().getItemMeta().getEnchants()); ;
        } else {
            enchantments = new HashMap<>(((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants());
        }

        plugin.getLogManager().logDebugConsole("Enchantments (Start) : " + enchantments.toString());

        ItemMeta meta = e.getResult().getItemMeta();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (!e.getResult().getItemMeta().getEnchants().isEmpty() && meta.getEnchants().containsKey(enchantment.getKey())) {
                meta.removeEnchant(enchantment.getKey());
            } else if (((EnchantmentStorageMeta)meta).getStoredEnchants().containsKey(enchantment.getKey())) {
                ((EnchantmentStorageMeta)meta).removeStoredEnchant(enchantment.getKey());
            }
        }


        Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                plugin.getLogManager().logDebugConsole("Removed : " + ChatColor.GREEN + enchantment.getKey().getKey());
                continue;
            } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                plugin.getLogManager().logDebugConsole("Replaced : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ". Level was : " + ChatColor.GREEN + enchantment.getValue() + ChatColor.WHITE + " and is now : " + ChatColor.GREEN);
                finalEnchantments.put(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
            } else {
                finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
            }
        }

        plugin.getLogManager().logDebugConsole("Enchantments (End) : " + finalEnchantments.toString());

        if (finalEnchantments.isEmpty()) {
            e.setResult(null);
        } else {
            for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
                    meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                } else {
                    ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                }
            }
            e.getResult().setItemMeta(meta);
            if (!e.getResult().getItemMeta().getEnchants().isEmpty()) {
                plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + e.getResult().getItemMeta().getEnchants().toString());
            } else {
                plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)e.getResult().getItemMeta()).getStoredEnchants().toString());
            }
        }
    }

    private Map<Enchantment, Integer> computeFusion(ItemStack[] items) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        Map<Enchantment, Integer> lhsEnchantments = null;
        Map<Enchantment, Integer> rhsEnchantments = null;
        boolean isFusionPossible = false;
        
        for (int i = 0; i < 2; i++) {
            if (items[i] != null && items[i].getItemMeta() != null && 
                (!items[i].getItemMeta().getEnchants().isEmpty() || 
                (items[i].getItemMeta() instanceof EnchantmentStorageMeta &&
                !((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().isEmpty()))) {
                isFusionPossible = true;
            }
        }
        if (!isFusionPossible)
            return (null);
        
        if (!items[0].getItemMeta().getEnchants().isEmpty()) {
            lhsEnchantments = new HashMap<>(items[0].getItemMeta().getEnchants()); ;
        } else if (items[0].getItemMeta() instanceof EnchantmentStorageMeta &&
                !((EnchantmentStorageMeta)items[0].getItemMeta()).getStoredEnchants().isEmpty()) {
            lhsEnchantments = new HashMap<>(((EnchantmentStorageMeta)items[0].getItemMeta()).getStoredEnchants());
        } else {
            lhsEnchantments = new HashMap<>();
        }

        if (!items[1].getItemMeta().getEnchants().isEmpty()) {
            rhsEnchantments = new HashMap<>(items[1].getItemMeta().getEnchants()); ;
        } else if (items[1].getItemMeta() instanceof EnchantmentStorageMeta &&
            !((EnchantmentStorageMeta)items[1].getItemMeta()).getStoredEnchants().isEmpty()) {
            rhsEnchantments = new HashMap<>(((EnchantmentStorageMeta)items[1].getItemMeta()).getStoredEnchants());
        } else {
            rhsEnchantments = new HashMap<>();
        }

        for (Map.Entry<Enchantment, Integer> enchantment : lhsEnchantments.entrySet()) {
            if (rhsEnchantments.containsKey(enchantment.getKey()) &&
                enchantment.getValue() == rhsEnchantments.get(enchantment.getKey()) &&
                enchantment.getValue() < plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                enchantments.put(enchantment.getKey(), enchantment.getValue() + 1);
            } else {
                enchantments.put(enchantment.getKey(), enchantment.getValue());
            }
        }

        boolean conflicts = false;
        for (Map.Entry<Enchantment, Integer> enchantment : rhsEnchantments.entrySet()) {
            if (!enchantments.containsKey(enchantment.getKey())) {
                conflicts = false;
                for (Map.Entry<Enchantment, Integer> current : enchantments.entrySet()) {
                    if (current.getKey().conflictsWith(enchantment.getKey())) {
                        conflicts = true;
                        break;
                    }
                }
                if (conflicts == false ) {
                    enchantments.put(enchantment.getKey(), enchantment.getValue());
                }
            }
        }
        return (enchantments);
    }
}
