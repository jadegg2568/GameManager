package com.jadegg.gamemanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.*;

public class Utils {

    public static void broadcastAll(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    public static void playSoundAll(Sound sound, float v1, float v2) {
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), sound, v1, v2));
    }

    public static String toMMSS(int time) {
        return (String.valueOf(time / 60).length() == 1 ? "0" + (time / 60) : time / 60)
                + ":"
                + (String.valueOf(time % 60).length() == 1 ? "0" + (time % 60) : time % 60);
    }

    public static String getDate() {
        Date date = new Date();
        int day = date.getDay();
        int month = date.getMonth();
        int year = date.getYear();

        return (String.valueOf(day).length() == 1 ? "0" + day : day) + "/" +
            (String.valueOf(month).length() == 1 ? "0" + month : month) + "/" +
            (String.valueOf(year).length() == 1 ? "0" + year : year);
    }
}
