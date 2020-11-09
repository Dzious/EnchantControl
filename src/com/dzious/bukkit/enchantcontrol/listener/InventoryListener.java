package com.dzious.bukkit.enchantcontrol.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

public class InventoryListener implements Listener {
    final EnchantControl plugin;

    public InventoryListener(EnchantControl plugin) 
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerOpenInventory(InventoryOpenEvent e)
    {
        ItemStack[] items = e.getInventory().getContents();

        for (int i = 0; i < items.length; i++) {
            if ((items[i].getEnchantments().isEmpty() == true && (items[i].getItemMeta() == null || (items[i].getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)(items[i].getItemMeta())).getStoredEnchants().isEmpty() == true))) {
                return;
            };

            if (items[i].getEnchantments().isEmpty() == false) {
                plugin.getLogManager().logInfo("Enchantments : " +  items[i].getEnchantments());
                for (Map.Entry<Enchantment, Integer> enchantment : items[i].getEnchantments().entrySet()) {
                    if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                        plugin.getLogManager().logInfo("Enchantments : Remove");
                        items[i].removeEnchantment(enchantment.getKey());
                    } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                        plugin.getLogManager().logInfo("Enchantments : Replace");
                        items[i].removeEnchantment(enchantment.getKey());
                        items[i].addEnchantment(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())); // TODO may have to reset item (e.setResult(ItemStack))
                    }
                }
            } else {
                plugin.getLogManager().logInfo("Stored Enchantments : " +  ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants());
                for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants().entrySet()) {
                    if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                        plugin.getLogManager().logInfo("Stored Enchantments : Remove");
                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)items[i].getItemMeta());
                        meta.removeStoredEnchant(enchantment.getKey());
                        items[i].setItemMeta(meta);
                    } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                        plugin.getLogManager().logInfo("Stored Enchantments : Replace");
                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)items[i].getItemMeta());
                        meta.removeStoredEnchant(enchantment.getKey());
                        meta.addStoredEnchant(enchantment.getKey(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), true); // TODO may have to reset item (e.setResult(ItemStack))
                        items[i].setItemMeta(meta);
                    }
                }
                if (items[i].getType() == Material.ENCHANTED_BOOK && ((EnchantmentStorageMeta)(items[i].getItemMeta())).getStoredEnchants().isEmpty() == true) {
                    items[i] = new ItemStack(Material.BOOK);
                } else {
                    plugin.getLogManager().logInfo("Stored Enchantments : " +  ((EnchantmentStorageMeta)items[i].getItemMeta()).getStoredEnchants());
                }
            }
            e.getInventory().setContents(items);
        }
    }
}
