package com.dzious.bukkit.enchantcontrol.listener;

import com.dzious.bukkit.enchantcontrol.EnchantControl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEnchantmentListener implements Listener {
    private EnchantControl plugin;

    public PlayerEnchantmentListener(EnchantControl plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        plugin.getPlayersManager().registerPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        plugin.getPlayersManager().removePlayer(e.getPlayer().getUniqueId());
    }
}
