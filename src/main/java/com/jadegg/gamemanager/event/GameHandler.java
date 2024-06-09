package com.jadegg.gamemanager.event;

import com.jadegg.gamemanager.manage.MechanicManager;
import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.util.game.GameState;
import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.manage.SettingManager;
import com.jadegg.gamemanager.manage.ScoreboardManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class GameHandler implements Listener {

    private Map<String, Long> gunCooldown = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setJoinMessage(null);
        Player p = event.getPlayer();

        ScoreboardManager.register(p.getName());
        if (Main.getGameManager().isEnabled())
            Main.getGameManager().addPlayer(p.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        event.setQuitMessage(null);
        Player p = event.getPlayer();

        ScoreboardManager.unregister(p.getName());
        if (Main.getGameManager().isEnabled())
            Main.getGameManager().removePlayer(p.getName());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player p = (Player)event.getEntity();
        EntityDamageEvent.DamageCause damageCause = event.getCause();

        GameManager gameManager = Main.getGameManager();
        if (!gameManager.getPlayers().contains(p.getName())) return;

        switch (damageCause) {
            case ENTITY_ATTACK:
            case ENTITY_EXPLOSION:
            case ENTITY_SWEEP_ATTACK:
                break;
            case FIRE:
                event.setCancelled(true);
                break;
            case LIGHTNING:
                event.setCancelled(true);
                break;
            case FALL:
                event.setCancelled(!SettingManager.fallDamage);
                break;
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(damager instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        if (!(entity instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        Player p1 = (Player)damager;
        Player p2 = (Player)entity;
        double damage = event.getDamage();

        GameManager gameManager = Main.getGameManager();
        if (!gameManager.getPlayers().contains(p1.getName())
                || !gameManager.getPlayers().contains(p2.getName())) return;

        if (!SettingManager.pvpEnabled) {
            event.setCancelled(true);
            return;
        }

        if (damage >= p2.getHealth() && Main.getGameManager().getGameState() == GameState.GAME) {
            event.setCancelled(true);
             MechanicManager.playerDied(Main.getGameManager(), p2.getName(), p1.getName());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player p = event.getEntity();

        event.setCancelled(true);
        if (Main.getGameManager().getGameState() == GameState.GAME)
            MechanicManager.playerDied(Main.getGameManager(), p.getName(), null);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (item == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (item.getType() == Material.BED) {
            p.kickPlayer("Вы вышли.");
        } else if (item.getType() == Material.IRON_HOE) {
            if (Main.getGameManager().getGameState() != GameState.GAME || !SettingManager.gunsEnabled) return;

            if (gunCooldown.containsKey(p.getName())) {
                double timePassed = (double) (System.currentTimeMillis() - gunCooldown.get(p.getName())) / 1000;
                if (timePassed >= 0.1) {
                    gunCooldown.put(p.getName(), System.currentTimeMillis());
                    shoot(p);
                }
            } else {
                gunCooldown.put(p.getName(), System.currentTimeMillis());
                shoot(p);
            }
        }
    }

    public void shoot(Player p) {
        p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1, 2);
        Location loc = p.getLocation().add(0, 1.25, 0);
        Vector vector = loc.getDirection();
        for (int i = 0; i < 35; i++) {
            loc = loc.add(vector.getX() / 1.7, vector.getY() / 1.7, vector.getZ() / 1.7);
            loc.getWorld().spigot().playEffect(loc, Effect.FLAME, 0, 0, 0, 0, 0, 0, 1, 50);

            if (!loc.getBlock().isLiquid() && (loc.getBlock().getType() != Material.AIR)) return;

            for (Entity entity : loc.getNearbyEntities(1, 2, 1)) {
                if (!(entity instanceof LivingEntity)) continue;
                if (!(entity instanceof Player)) continue;
                Player pl = (Player)entity;
                if (pl.getName().equals(p.getName())) continue;
                if (Main.getGameManager().getGameTeam(p.getName()).getPlayers().contains(pl.getName())) return;

                double distanceSquared = entity.getLocation().distanceSquared(loc);
                if (distanceSquared <= 1.25) {
                    if (pl.getHealth() <= 2) {
                        MechanicManager.playerDied(Main.getGameManager(), pl.getName(), p.getName());
                    } else {
                        pl.damage(2);
                    }
                    return;
                }
            }
        }
    }
}
