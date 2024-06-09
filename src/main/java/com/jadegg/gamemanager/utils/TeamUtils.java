package com.jadegg.gamemanager.utils;

public class TeamUtils {

    public static char getColorCode(String name) {
        switch (name) {
            case "red": return 'c';
            case "blue": return '9';
            case "green": return 'a';
            case "yellow": return 'e';
            default: return 'f';
        }
    }
}
