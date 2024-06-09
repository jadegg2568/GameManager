package com.jadegg.gamemanager.util.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class GameTeam {

    private String name;
    private List<String> players;
    
    private Location spawn;

    private boolean isAlive;

    public GameTeam(String name, List<String> players, Location spawn, boolean isAlive) {
        this.name = name;
        this.players = players;

        this.spawn = spawn;

        this.isAlive = isAlive;
    }

    public void teleportToSpawn() {
        players.forEach(nickname -> Bukkit.getPlayer(nickname).teleport(spawn));
    }

    public void add(String nickname) {
        players.add(nickname);
    }

    public void remove(String nickname) {
        players.remove(nickname);
        if (players.size() == 0)
            isAlive = false;
    }

    public int size() {
        return players.size();
    }

    public String getName() {
        return name;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}
