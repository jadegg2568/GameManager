package com.jadegg.gamemanager.manage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private static String configPath = "plugins/GameManager/config.yml";
    private static File configFile = new File(configPath);
    private static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void setupSettings() {
        SettingManager.name = config.getString("name");
        SettingManager.minimum = config.getInt("minimum");
        SettingManager.maximum = config.getInt("maximum");
        SettingManager.teamCount = config.getInt("teamCount");
        SettingManager.playersPerTeam = config.getInt("playersPerTeam");
        SettingManager.isEnabled = config.getBoolean("isEnabled");
        SettingManager.instantStart = config.getBoolean("instantStart");

        SettingManager.objectiveName = config.getString("objectiveName");
        SettingManager.objectiveCredit = config.getString("objectiveCredit");

        SettingManager.canItemDrop = config.getBoolean("canItemDrop");
        SettingManager.canBlockExploded = config.getBoolean("canBlockExploded");
        SettingManager.canBlockBreak = config.getBoolean("canBlockBreak");
        SettingManager.canBlockPlace = config.getBoolean("canBlockPlace");
        SettingManager.hungerEnabled = config.getBoolean("hungerEnabled");
        SettingManager.pvpEnabled = config.getBoolean("pvpEnabled");
        SettingManager.fallDamage = config.getBoolean("fallDamage");
        SettingManager.gunsEnabled = config.getBoolean("gunsEnabled");

        SettingManager.countdownTime = config.getInt("countdownTime");
        SettingManager.gameTime = config.getInt("gameTime");
    }
}
