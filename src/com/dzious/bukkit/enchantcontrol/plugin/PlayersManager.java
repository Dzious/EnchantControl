package com.dzious.bukkit.enchantcontrol.plugin;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayersManager {
    Map<UUID, EnchantmentPlayer> players = new HashMap<>();

    public void registerPlayer(UUID uuid) {
        players.put(uuid, new EnchantmentPlayer());
    }

    public void removePlayer(UUID uuid) {
        players.get(uuid);
        players.remove(uuid);
    }

    public EnchantmentPlayer getPlayer(UUID uuid) {
        return (players.get(uuid));
    }
}
