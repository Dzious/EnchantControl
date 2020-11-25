package com.dzious.bukkit.enchantcontrol.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;

public class EnchantmentPlayer {
    List<Map<Enchantment,EnchantmentOffer>> registeredOffers = new ArrayList<>();

    public void setOffers(EnchantmentOffer[] newOffers, List<Enchantment> oldOffers)
    {
        registeredOffers.clear();
        for (int i = 0; i < newOffers.length; i++) {
            Map<Enchantment, EnchantmentOffer> offer = new HashMap<>();
            offer.put(oldOffers.get(i), newOffers[i]);
            registeredOffers.add(offer);
        }
    }

    public boolean hasOffers() {
        return (!registeredOffers.isEmpty());
    }

    public void clearOffers() {
        registeredOffers.clear();
    }
    
    public Map<Enchantment, EnchantmentOffer> getOffers(int requestedIdx) {
        Map<Enchantment, EnchantmentOffer> offers = registeredOffers.get(requestedIdx);

        registeredOffers.clear();
        return(offers);
    }
}
