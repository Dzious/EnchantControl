package com.dzious.bukkit.enchantcontrol.listener;

import java.util.HashMap;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

public class MobsLootListener implements Listener {
    final EnchantControl plugin;

    public MobsLootListener(EnchantControl plugin) 
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onEntityDeath(EntityDeathEvent e) {
        ItemStack[] items = new ItemStack[e.getDrops().size()];

        
        for (int i = 0; i < e.getDrops().size(); i++) {
            items[i] = e.getDrops().get(i);
        }
        
        // e.getDrops().toArray(new ItemStack[0]);

        items = removeEnchants(items);

        e.getDrops().clear();            
        for (ItemStack item : items) {
            e.getDrops().add(item);
        }

        return;
    }

    

    ItemStack[] removeEnchants(ItemStack[] items) 
    {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || 
                items[i].getType() == Material.SHULKER_BOX || 
                items[i].getItemMeta() == null || 
                (items[i].getEnchantments().isEmpty() &&
                (!(items[i].getItemMeta() instanceof EnchantmentStorageMeta) ||
                ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().isEmpty()))) {
                continue;
            }

            plugin.getLogManager().logDebugConsole("Item[" + i + "] type : " +  items[i].getType());

            Map<Enchantment, Integer> enchantments;
            if (!items[i].getItemMeta().getEnchants().isEmpty()) {
                enchantments = new HashMap<>(items[i].getItemMeta().getEnchants()); ;
            } else {
                enchantments = new HashMap<>(((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants());
            }

            plugin.getLogManager().logDebugConsole("Enchantments (Start) : " + enchantments.toString());

            ItemMeta meta = items[i].getItemMeta();
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                if (!items[i].getItemMeta().getEnchants().isEmpty() && meta.getEnchants().containsKey(enchantment.getKey())) {
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
                    PersistentDataContainer container = items[i].getItemMeta().getPersistentDataContainer();
                    if (container.has(plugin.getNamespacedKey(), PersistentDataType.STRING) && container.get(plugin.getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("event")) {
                        plugin.getLogManager().logDebugConsole("Skipped : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ".");
                        continue;
                    }
                    plugin.getLogManager().logDebugConsole("Replaced : " + ChatColor.GREEN + enchantment.getKey().getKey() + ChatColor.WHITE + ". Level was : " + ChatColor.GREEN + enchantment.getValue() + ChatColor.WHITE + " and is now : " + ChatColor.GREEN);
                    finalEnchantments.put(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                } else {
                    finalEnchantments.put(enchantment.getKey(),enchantment.getValue());
                }
            }

            plugin.getLogManager().logDebugConsole("Enchantments (End) : " + finalEnchantments.toString());

            if (finalEnchantments.isEmpty() && items[i].getType() == Material.ENCHANTED_BOOK) {
                items[i] = new ItemStack(Material.BOOK);
            } else {
                for (Map.Entry<Enchantment, Integer> enchantment : finalEnchantments.entrySet()) {
                    plugin.getLogManager().logDebugConsole("Enchantment (Loop) : " + finalEnchantments.toString());
                    if (!items[i].getItemMeta().getEnchants().isEmpty()) {
                        meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
                    } else {
                        ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment.getKey(), enchantment.getValue(), true);
                    }
                }

                plugin.getLogManager().logDebugConsole("Meta (End) : " + meta);


                items[i].setItemMeta(meta);
                if (!items[i].getItemMeta().getEnchants().isEmpty()) {
                    plugin.getLogManager().logDebugConsole("Enchantments (Meta) : " + items[i].getItemMeta().getEnchants().toString());
                } else if (meta instanceof EnchantmentStorageMeta ) {
                    plugin.getLogManager().logDebugConsole("Stored Enchantments (Meta) : " + ((EnchantmentStorageMeta)(items[i].getItemMeta())).getStoredEnchants().toString());
                } else {
                    plugin.getLogManager().logDebugConsole("Item has no more enchantments!");
                }
            }
        }
        return (items);
    }
}