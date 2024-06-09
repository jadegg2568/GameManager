package com.jadegg.gamemanager.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class PlayerScoreboard {

    private final String nickname;
    private Scoreboard scoreboard;

    public PlayerScoreboard(String nickname) {
        this.nickname = nickname;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Bukkit.getPlayer(nickname).setScoreboard(scoreboard);
    }

    public void createTeam(String name, String prefix, String suffix, List<String> players) {
        Team team = scoreboard.registerNewTeam(name);
        if (prefix != null) team.setPrefix(prefix);
        if (suffix != null) team.setSuffix(suffix);
        if (players != null) players.stream().map(Bukkit::getPlayer).forEach(p -> team.addPlayer(p));
    }

    public void removeTeam(String name) {
        scoreboard.getTeam(name).unregister();
    }

    public void joinToTeam(String team, String nickname) {
        scoreboard.getTeam(team).addPlayer(Bukkit.getPlayer(nickname));
    }

    public void removeFromTeam(String team, String nickname) {
        scoreboard.getTeam(team).removePlayer(Bukkit.getPlayer(nickname));
    }

    public void changeTeam(String team, String nickname) {
        Player p = Bukkit.getPlayer(nickname);
        scoreboard.getPlayerTeam(p).removePlayer(p);
        scoreboard.getTeam(team).addPlayer(p);
    }

    public void createObjective(String name, String displayName) {
        Objective obj = scoreboard.registerNewObjective(name, "dummy");
        obj.setDisplayName(displayName);
    }

    public void showObjective(String name, DisplaySlot displaySlot) {
        Objective obj = scoreboard.getObjective(name);
        obj.setDisplaySlot(displaySlot);
    }

    public boolean hasObjective(String name) {
        return scoreboard.getObjective(name) != null;
    }

    public void addCustomizableField(String objName, String name, String key, String prefix, String suffix, int score) {
        Team team = scoreboard.getTeam(name);
        if (team == null) team = scoreboard.registerNewTeam(name);
        team.addEntry(key);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        scoreboard.getObjective(objName).getScore(key).setScore(score);
    }

    public void changePrefix(String field, String prefix) {
        scoreboard.getTeam(field).setPrefix(prefix);
    }

    public void changeSuffix(String field, String suffix) {
        scoreboard.getTeam(field).setSuffix(suffix);
    }

    public void reset() {
        scoreboard.getObjectives().forEach(Objective::unregister);
        scoreboard.getTeams().forEach(Team::unregister);
    }

    public void resetObjectives() {
        scoreboard.getObjectives().forEach(Objective::unregister);
    }

    public void resetTeams() {
        scoreboard.getTeams().forEach(Team::unregister);
    }

    public void delete() {
        Bukkit.getPlayer(nickname).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

}
