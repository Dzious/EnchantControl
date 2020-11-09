package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishingListener implements Listener {
    private final EnchantControl plugin;

    public FishingListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFishTreasure(PlayerFishEvent e)
    {
        if (e.getCaught() == null || (e.getCaught() instanceof Item) == false) {
            if (e.getCaught() == null)
                plugin.getLogManager().logDebugConsole("Nothing caught");
            else
                plugin.getLogManager().logDebugConsole("caught thing was not an enchantable item");
            return;
        }
        Item item = (Item)(e.getCaught());


        if (item.getItemStack().getEnchantments().isEmpty() == true && (item.getItemStack().getItemMeta() == null || (item.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) == false || ((EnchantmentStorageMeta)(item.getItemStack().getItemMeta())).getStoredEnchants().isEmpty() == true)) {
            plugin.getLogManager().logDebugConsole("");
            return;
        }

        plugin.getLogManager().logDebugConsole("Caught : " + ((Item)e.getCaught()).getItemStack());

        if (((Item)e.getCaught()).getItemStack().getEnchantments().isEmpty() == false) {
            List<Enchantment> enchantmentsList = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> enchantment : ((Item)e.getCaught()).getItemStack().getEnchantments().entrySet()) {
                enchantmentsList.add(enchantment.getKey());
            }

            for (Map.Entry<Enchantment, Integer> enchantment : ((Item)e.getCaught()).getItemStack().getEnchantments().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(((Item)e.getCaught()).getItemStack(), enchantmentsList);
                    Integer enchantmentLevel = enchantment.getValue();

                    ((Item)e.getCaught()).getItemStack().removeEnchantment(enchantment.getKey());
                    if (newEnchantment != null) {
                        if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                            enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                        ((Item)e.getCaught()).getItemStack().addEnchantment(newEnchantment, enchantmentLevel);
                    }
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    ((Item)e.getCaught()).getItemStack().removeEnchantment(enchantment.getKey());
                    ((Item)e.getCaught()).getItemStack().addEnchantment(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
                }
            }
        } else {
            List<Enchantment> enchantmentsList = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta)(((Item)(e.getCaught())).getItemStack().getItemMeta())).getStoredEnchants().entrySet()) {
                enchantmentsList.add(enchantment.getKey());
            }

            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta)(((Item)(e.getCaught())).getItemStack().getItemMeta())).getStoredEnchants().entrySet()) {
                if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)((Item)e.getCaught()).getItemStack().getItemMeta());
                    Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(((Item)e.getCaught()).getItemStack(), enchantmentsList);
                    Integer enchantmentLevel = enchantment.getValue();
                    meta.removeStoredEnchant(enchantment.getKey());
                    if (newEnchantment != null) {
                        if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                            enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                        meta.addStoredEnchant(newEnchantment, enchantmentLevel, false);
                    }
                    ((Item)e.getCaught()).getItemStack().setItemMeta(meta);
                } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey())) {
                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta)((Item)e.getCaught()).getItemStack().getItemMeta());
                    meta.removeStoredEnchant(enchantment.getKey());
                    meta.addStoredEnchant(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()), false);
                    ((Item)e.getCaught()).getItemStack().setItemMeta(meta);
                }
            }
            if (((Item)e.getCaught()).getItemStack().getType() == Material.ENCHANTED_BOOK && ((EnchantmentStorageMeta)((Item)e.getCaught()).getItemStack().getItemMeta()).getStoredEnchants().isEmpty() == true)
                ((Item)e.getCaught()).setItemStack(new ItemStack(Material.BOOK));
        }
    }

}