package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishingListener implements Listener {
    private EnchantControl plugin;

    public FishingListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantmentPreparation(PlayerFishEvent e)
    {
        if (e.getCaught() == null || e.getCaught() instanceof Item == false) {
            return;
        }
        List<Enchantment> enchantmentsList = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> enchantment : ((Item)e.getCaught()).getItemStack().getEnchantments().entrySet()) {
            enchantmentsList.add(enchantment.getKey());
        }

        for (Map.Entry<Enchantment, Integer> enchantment : ((Item)e.getCaught()).getItemStack().getEnchantments().entrySet()) {
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(((Item)e.getCaught()).getItemStack(), enchantmentsList);
                Integer enchantmentLevel = enchantment.getValue();

                ((Item)e.getCaught()).getItemStack().getEnchantments().remove(enchantment);
                if (newEnchantment != null) {
                    if (enchantmentLevel > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                        enchantmentLevel = plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment);
                    ((Item)e.getCaught()).getItemStack().getEnchantments().put(newEnchantment, enchantmentLevel);
                }
            } else if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()))
                ((Item)e.getCaught()).getItemStack().getEnchantments().replace(enchantment.getKey(),plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
        }

        if (((Item)e.getCaught()).getItemStack().getEnchantments().isEmpty() == true)
            ((Item)e.getCaught()).setItemStack(new ItemStack(Material.BOOK));

        return;
    }
}


