package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentTableListener implements Listener {
    private EnchantControl plugin;

    public EnchantmentTableListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantmentPreparation(PrepareItemEnchantEvent e)
    {
        if (plugin.getEnchantmentManager().hasEnchantActive(e.getItem()) == false) {
            //e.setCancelled(true);
            return;
        }

        List<Enchantment> offersList = new ArrayList<>();
        for (EnchantmentOffer offer : e.getOffers()) {
            if (offer != null)
                offersList.add(offer.getEnchantment());
        }

        for (EnchantmentOffer offer : e.getOffers()) {
            if (offer == null)
               continue;
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(offer.getEnchantment()) <= 0) {
                Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(e.getItem(), offersList);

                offer.setEnchantment(newEnchantment);
                if (newEnchantment != null) {
                    if (offer.getEnchantmentLevel() > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                        offer.setEnchantmentLevel(plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment));
                } else {
                    offer.setEnchantmentLevel(0);
                }
            } else if (plugin.getEnchantmentManager().getAffectedEnchantments().get(offer.getEnchantment()) < offer.getEnchantmentLevel()){
                    offer.setEnchantmentLevel(plugin.getEnchantmentManager().getAffectedEnchantments().get(offer.getEnchantment()));
            }
        }
        for (EnchantmentOffer offer : e.getOffers())
            if (offer != null)
                plugin.getLogManager().logDebugConsole(ChatColor.GREEN +  offer.getEnchantment().toString());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEnchantItem(EnchantItemEvent e) {
        plugin.getLogManager().logDebugConsole(ChatColor.RED +  e.getEnchantsToAdd().toString());
    }
}
