package com.jadegg.gamemanager.manage;

import com.google.common.collect.Lists;
import com.jadegg.gamemanager.util.PlayerScoreboard;
import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.util.game.GameTeam;
import com.jadegg.gamemanager.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {

    public static void distributeIntoTeams(GameManager gameManager) {
        List<String> totalPlayers = new ArrayList<>(gameManager.getPlayers());
        Collections.shuffle(totalPlayers);

        Map<String, GameTeam> gameTeams = new HashMap<>();

        switch (gameManager.getTeamCount()) {
            case 8:
                break;
            case 4:
                gameTeams.put("green", new GameTeam("green", Lists.newArrayList(), gameManager.getLocationMap().get("greenSpawn"), false));
                gameTeams.put("yellow", new GameTeam("yellow", Lists.newArrayList(), gameManager.getLocationMap().get("yellowSpawn"), false));
            case 2:
                gameTeams.put("red", new GameTeam("red", Lists.newArrayList(), gameManager.getLocationMap().get("redSpawn"), false));
                gameTeams.put("blue", new GameTeam("blue", Lists.newArrayList(), gameManager.getLocationMap().get("blueSpawn"), false));
                break;
        }

        while (totalPlayers.size() != 0) {
            String nickname = totalPlayers.get(0);
            totalPlayers.remove(nickname);
            Optional<GameTeam> freeTeamOptional = gameTeams.values().stream().filter(gameTeam -> gameTeam.size() < gameManager.getPlayersPerTeam()).findFirst();
            Player p = Bukkit.getPlayer(nickname);
            if (freeTeamOptional.isPresent()) {
                GameTeam freeTeam = freeTeamOptional.get();
                if (!freeTeam.isAlive()) freeTeam.setAlive(true);
                freeTeam.add(nickname);
                p.sendMessage("§eВы попали в команду §" + TeamUtils.getColorCode(freeTeam.getName()) + freeTeam.getName());
            } else {
                gameManager.removePlayer(nickname);
                p.kickPlayer("§cПроизошла ошибка в распределении на команды:\nВсе команды переполнены!");
            }
        }

        gameManager.setGameTeams(gameTeams);
    }

    public static int aliveTeamCount(GameManager gameManager) {
        return (int)gameManager.getGameTeams().values().stream().filter(GameTeam::isAlive).count();
    }
}
