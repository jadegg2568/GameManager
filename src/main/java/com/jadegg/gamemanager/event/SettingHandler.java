package com.jadegg.gamemanager.event;

import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.manage.SettingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class SettingHandler implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setCancelled(!SettingManager.canItemDrop);
    }

    @EventHandler
    public void blockExploded(BlockExplodeEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setCancelled(!SettingManager.canBlockBreak);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setCancelled(!SettingManager.canBlockPlace);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setCancelled(!SettingManager.hungerEnabled);
    }
}
