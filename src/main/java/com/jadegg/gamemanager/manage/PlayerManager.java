package com.jadegg.gamemanager.manage;

import com.jadegg.gamemanager.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerManager {

    public static void reset(Player p) {
        p.setFireTicks(0);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
    }

    public static void giveSelectInventory(Player p) {
        p.getInventory().clear();
        p.getInventory().setItem(8, ItemUtils.namedItem(new ItemStack(Material.BED), "§eВыйти"));
    }

    public static void giveGameInventory(Player p) {
        p.getInventory().clear();

        p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        p.getInventory().addItem(ItemUtils.namedItem(new ItemStack(Material.IRON_HOE), "§eАК-47"));
    }

    public static void clearInventory(Player p) {
        p.getInventory().clear();
    }
}
