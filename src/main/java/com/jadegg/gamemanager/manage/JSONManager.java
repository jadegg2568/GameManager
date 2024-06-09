package com.jadegg.gamemanager.manage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JSONManager {

    private static String locationsPath = "plugins/GameManager/locations.json";
    private static File locationsFile = new File(locationsPath);

    public static void createLocationsFile() {
        try {
            File parentDir = locationsFile.getParentFile();

            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!locationsFile.exists()) {
                locationsFile.createNewFile();
            }

            try (FileWriter fileWriter = new FileWriter(locationsFile)) {
                fileWriter.write("[]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveLocations(Map<String, Location> locationMap) {
        try {
            if (!locationsFile.exists()) {
                createLocationsFile();
            }

            JSONArray jsonArray = new JSONArray();
            for (String locationName : locationMap.keySet()) {
                Location location = locationMap.get(locationName);
                jsonArray.add(locationToJSONObject(locationName, location));
            }

            writeJSONArrayToFile(jsonArray, locationsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Location> loadLocations() {
        Map<String, Location> locationMap = new HashMap<>();

        try {
            if (!locationsFile.exists()) {
                createLocationsFile();
                return locationMap;
            }

            JSONArray jsonArray = readJSONArrayFromFile(locationsFile);
            if (jsonArray.size() == 0) return locationMap;

            for (Object object : jsonArray.toArray()) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                Location location = JSONObjectToLocation(jsonObject);
                locationMap.put(name, location);
            }
            return locationMap;
        } catch (Exception e) {
            e.printStackTrace();
            return locationMap;
        }
    }

    public static void writeJSONArrayToFile(JSONArray jsonArray, File file) {
        if (jsonArray.size() == 0) {
            return;
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray readJSONArrayFromFile(File file) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            return (JSONArray)parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject locationToJSONObject(String name, Location location) {
        JSONObject locationObj = new JSONObject();
        locationObj.put("name", name);
        locationObj.put("world", location.getWorld().getName());
        locationObj.put("x", location.getBlockX());
        locationObj.put("y", location.getBlockY());
        locationObj.put("z", location.getBlockZ());
        locationObj.put("yaw", location.getYaw());
        locationObj.put("pitch", location.getPitch());
        return locationObj;
    }

    public static Location JSONObjectToLocation(JSONObject jsonObject) {
        String world = (String)jsonObject.get("world");
        int x = Integer.parseInt(jsonObject.get("x").toString());
        int y = Integer.parseInt(jsonObject.get("y").toString());
        int z = Integer.parseInt(jsonObject.get("z").toString());
        float yaw = Float.parseFloat(jsonObject.get("yaw").toString());
        float pitch = Float.parseFloat(jsonObject.get("pitch").toString());
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
