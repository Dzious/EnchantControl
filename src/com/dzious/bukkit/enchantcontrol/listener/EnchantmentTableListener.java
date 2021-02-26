package com.dzious.bukkit.enchantcontrol.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class EnchantmentTableListener implements Listener {
    private final EnchantControl plugin;

    public EnchantmentTableListener (EnchantControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantmentPreparation(PrepareItemEnchantEvent e)
    {
        if (e.getItem().getType() != Material.BOOK && !plugin.getEnchantmentManager().hasEnchantActive(e.getItem()))  {
            return;
        }

        plugin.getLogManager().logDebugConsole("Generating offers : ");

        List<Enchantment> offersList = new ArrayList<>();
        for (int i = 0; i < e.getOffers().length; i++) {
            if (e.getOffers()[i] != null) {
                plugin.getLogManager().logDebugConsole("Offer " + i + " is "  + ChatColor.GREEN + e.getOffers()[i].getEnchantment().toString());
                offersList.add(e.getOffers()[i].getEnchantment());
            } else {
                plugin.getLogManager().logDebugConsole("Offer " + i + " is " + ChatColor.GREEN + "null");
                offersList.add(null);
            }
        }

        for (int i = 0; i < e.getOffers().length; i++) {
            if (e.getOffers()[i] == null)
               continue;
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(e.getOffers()[i].getEnchantment()) <= 0) {

                Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(e.getItem(), offersList);

                if (newEnchantment != null) {
                    e.getOffers()[i].setEnchantment(newEnchantment);
                    if (e.getOffers()[i].getEnchantmentLevel() > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                        e.getOffers()[i].setEnchantmentLevel(plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment));
                } else {
                    e.getOffers()[i] = null; //.setEnchantmentLevel(0);
                }
            } else if (plugin.getEnchantmentManager().getAffectedEnchantments().get(e.getOffers()[i].getEnchantment()) < e.getOffers()[i].getEnchantmentLevel()){
                e.getOffers()[i].setEnchantmentLevel(plugin.getEnchantmentManager().getAffectedEnchantments().get(e.getOffers()[i].getEnchantment()));
            }
        }
        for (int i = 0; i < e.getOffers().length; i++) {
            if (e.getOffers()[i] != null) {
                plugin.getLogManager().logDebugConsole("Final offer " + i + " is "  + ChatColor.GREEN + e.getOffers()[i].getEnchantment().toString());
            } else {
                plugin.getLogManager().logDebugConsole("Final offer " + i + " is " + ChatColor.GREEN + "null");
            }
        }
        plugin.getLogManager().logDebugConsole("OffersList : " + offersList.toString());
        for (int i = 0; i < e.getOffers().length; i++) {
            plugin.getLogManager().logDebugConsole("Offer[" + i + "] is : " + e.getOffers()[i]);
        }
        plugin.getPlayersManager().getPlayer(e.getEnchanter().getUniqueId()).setOffers(e.getOffers(), offersList);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnchantItem(EnchantItemEvent e) {
        Map<Enchantment, EnchantmentOffer> offers = plugin.getPlayersManager().getPlayer(e.getEnchanter().getUniqueId()).getOffers(e.whichButton());

        for (Map.Entry<Enchantment, Integer> enchantment : e.getEnchantsToAdd().entrySet()) {
            if (offers.containsKey(enchantment.getKey())) {
                e.getEnchantsToAdd().remove(enchantment.getKey());
                e.getEnchantsToAdd().put(offers.get(enchantment.getKey()).getEnchantment(), offers.get(enchantment.getKey()).getEnchantmentLevel());
                break;
            }
        }

        List<Enchantment> enchantments = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> enchantment : e.getEnchantsToAdd().entrySet()) {
            enchantments.add(enchantment.getKey());
        }

        for (Map.Entry<Enchantment, Integer> enchantment : e.getEnchantsToAdd().entrySet()) {
            if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) <= 0) {
                Enchantment newEnchantment = plugin.getEnchantmentManager().rerollEnchantment(e.getItem(), enchantments);

                if (newEnchantment != null) {
                    Map<Enchantment, Integer> newApplicableEnchantment = new HashMap<>();
                    if (enchantment.getValue() > plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment))
                        newApplicableEnchantment.put(newEnchantment, plugin.getEnchantmentManager().getAffectedEnchantments().get(newEnchantment));
                    else
                        newApplicableEnchantment.put(newEnchantment, enchantment.getValue());
                }
                e.getEnchantsToAdd().remove(enchantment.getKey());
            } else if (plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()) < enchantment.getValue()){
                e.getEnchantsToAdd().replace(enchantment.getKey(), enchantment.getValue(), plugin.getEnchantmentManager().getAffectedEnchantments().get(enchantment.getKey()));
            }
        }

        plugin.getLogManager().logDebugConsole(ChatColor.BLUE + "Final Enchantments from " + e.whichButton() + " : ");
        plugin.getLogManager().logDebugConsole(ChatColor.GREEN +  e.getEnchantsToAdd().toString());
    }
}
