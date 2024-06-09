package com.jadegg.gamemanager.main;

import com.jadegg.gamemanager.command.GameManagerCommand;
import com.jadegg.gamemanager.event.ChatHandler;
import com.jadegg.gamemanager.event.SettingHandler;
import com.jadegg.gamemanager.event.GameHandler;
import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.manage.ConfigManager;
import com.jadegg.gamemanager.manage.SettingManager;
import com.jadegg.gamemanager.manage.JSONManager;
import com.jadegg.gamemanager.manage.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class Main extends JavaPlugin {

    private static Main instance;

    private static GameManager gameManager = null;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new GameHandler(), this);
        Bukkit.getPluginManager().registerEvents(new ChatHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SettingHandler(), this);
        getCommand("gamemanager").setExecutor(new GameManagerCommand());
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        ConfigManager.setupSettings();
        ScoreboardManager.registerAll();
        ScoreboardManager.startUpdater();

        getLogger().info("Plugin started.");
        getLogger().info("GameManager is null!");
        getLogger().info("Creating gameManager...");

        createGameManager();

        getLogger().fine("success.");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) JSONManager.saveLocations(gameManager.getLocationMap());
        instance = null;
    }



    public static Main getInstance() {
        return instance;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static void setGameManager(GameManager gameManager) {
        Main.gameManager = gameManager;
    }

    public static void createGameManager() {
        Map<String, Location> locationMap = JSONManager.loadLocations();

        gameManager = new GameManager(
                SettingManager.name,
                SettingManager.minimum,
                SettingManager.maximum,
                SettingManager.teamCount,
                SettingManager.playersPerTeam,
                SettingManager.instantStart);

        gameManager.setLocationMap(locationMap);
    }
}
