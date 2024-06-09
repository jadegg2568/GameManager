package com.jadegg.gamemanager.manage;

import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.util.game.GameState;
import com.jadegg.gamemanager.utils.Utils;
import com.jadegg.gamemanager.util.PlayerScoreboard;
import com.jadegg.gamemanager.util.thread.ScoreboardUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private static Map<String, PlayerScoreboard> scoreboardMap = new HashMap<>();
    private static ScoreboardUpdater scoreboardUpdater = new ScoreboardUpdater();

    public static void startUpdater() {
        scoreboardUpdater.start();
    }

    public static void cancelUpdater() {
        scoreboardUpdater.cancel();
    }

    public static void register(String nickname) {
        scoreboardMap.put(nickname, new PlayerScoreboard(nickname));
    }

    public static void registerAll() {
        for (Player p : Bukkit.getOnlinePlayers())
            scoreboardMap.put(p.getName(), new PlayerScoreboard(p.getName()));
    }

    public static void unregister(String nickname) {
        scoreboardMap.remove(nickname);
    }

    public static PlayerScoreboard getScoreboard(String nickname) {
        return scoreboardMap.get(nickname);
    }

    public static void createWaitingObjective(GameManager gameManager) {
        gameManager.getPlayers().forEach(nickname -> createWaitingObjective(gameManager, nickname));
        gameManager.getSpectators().forEach(nickname -> createWaitingObjective(gameManager, nickname));
    }

    public static void createGameObjective(GameManager gameManager) {
        gameManager.getPlayers().forEach(nickname -> createGameObjective(gameManager, nickname));
        gameManager.getSpectators().forEach(nickname -> createGameObjective(gameManager, nickname));
    }

    public static void reset(GameManager gameManager) {
        gameManager.getPlayers().forEach(nickname -> scoreboardMap.get(nickname).reset());;
        gameManager.getSpectators().forEach(nickname -> scoreboardMap.get(nickname).reset());;
    }

    public static void createWaitingObjective(GameManager gameManager, String nickname) {
        PlayerScoreboard playerScoreboard = scoreboardMap.get(nickname);
        playerScoreboard.resetObjectives();

        String objName = SettingManager.objectiveName;
        String date = Utils.getDate();
        String map = "NONE";
        String credit = SettingManager.objectiveCredit;
        int players = gameManager.getPlayers().size();
        int time = gameManager.getGameCountdown().getTime();
        GameState gameState = gameManager.getGameState();

        playerScoreboard.createObjective("waitingObjective", objName);

        playerScoreboard.addCustomizableField("waitingObjective", "date", "§8", "§7" + date, "", 8);
        playerScoreboard.addCustomizableField("waitingObjective", "emptyLine1", "§7", "", "", 7);
        playerScoreboard.addCustomizableField("waitingObjective", "players", "§6", "Игроков: ", "§a" + players + "/" + gameManager.getMaximum(), 6);
        playerScoreboard.addCustomizableField("waitingObjective", "map", "§5", "Карта: ", "§a" + map, 5);
        playerScoreboard.addCustomizableField("waitingObjective", "emptyLine2", "§4", "", "", 4);
        playerScoreboard.addCustomizableField("waitingObjective", "time", "§3", (gameState == GameState.WAITING ? "Ожидание..." : "Старт через "), (gameState == GameState.STARTING ? "§a" + time : ""), 3);
        playerScoreboard.addCustomizableField("waitingObjective", "emptyLine3", "§2", "", "", 2);
        playerScoreboard.addCustomizableField("waitingObjective", "credit", "§1", credit, "", 1);

        playerScoreboard.showObjective("waitingObjective", DisplaySlot.SIDEBAR);
    }

    public static void createGameObjective(GameManager gameManager, String nickname) {
        PlayerScoreboard playerScoreboard = scoreboardMap.get(nickname);
        playerScoreboard.resetObjectives();

        String objName = SettingManager.objectiveName;
        String date = Utils.getDate();
        String credit = SettingManager.objectiveCredit;
        int time = gameManager.getGameRunnable().getTime();
        int players = gameManager.getPlayers().size();

        playerScoreboard.createObjective("gameObjective", objName);

        playerScoreboard.addCustomizableField("gameObjective", "date", "§8", "§7" + date, "", 8);
        playerScoreboard.addCustomizableField("gameObjective", "emptyLine1", "§7", "", "", 7);
        playerScoreboard.addCustomizableField("gameObjective", "players", "§6", "Игроков: ", "§a" + players, 6);
        playerScoreboard.addCustomizableField("gameObjective", "aliveTeams", "§5", "Команд: ", "§a" + TeamManager.aliveTeamCount(gameManager), 5);
        playerScoreboard.addCustomizableField("gameObjective", "emptyLine2", "§4", "", "", 4);
        playerScoreboard.addCustomizableField("gameObjective", "time", "§3", "Конец через ", "§a" + time, 3);
        playerScoreboard.addCustomizableField("gameObjective", "emptyLine3", "§2", "", "", 2);
        playerScoreboard.addCustomizableField("gameObjective", "credit", "§1", credit, "", 1);

        playerScoreboard.showObjective("gameObjective", DisplaySlot.SIDEBAR);
    }

    public static void setupPrefixTeams(GameManager gameManager) {
        if (gameManager.getGameState() == GameState.GAME) {
            for (String nickname : gameManager.getPlayers()) {
                PlayerScoreboard playerScoreboard = ScoreboardManager.getScoreboard(nickname);
                playerScoreboard.createTeam("spectator", "§7[§cSPEC§7] ", null, null);
                switch (gameManager.getTeamCount()) {
                    case 8:
                        break;
                    case 4:
                        playerScoreboard.createTeam("green", "§a[G] ", null, gameManager.getGameTeams().get("green").getPlayers());
                        playerScoreboard.createTeam("yellow", "§e[Y] ", null, gameManager.getGameTeams().get("yellow").getPlayers());
                    case 2:
                        playerScoreboard.createTeam("red", "§c[R] ", null, gameManager.getGameTeams().get("red").getPlayers());
                        playerScoreboard.createTeam("blue", "§9[B] ", null, gameManager.getGameTeams().get("blue").getPlayers());
                        break;
                }
            }
        } else {
            for (String nickname : gameManager.getPlayers()) {
                PlayerScoreboard playerScoreboard = ScoreboardManager.getScoreboard(nickname);
                playerScoreboard.createTeam("player", "§7[Player] ", null, gameManager.getPlayers().stream().filter(_nickname -> !Bukkit.getPlayer(_nickname).isOp()).collect(Collectors.toList()));
                playerScoreboard.createTeam("admin", "§c[ADMIN] ", null, gameManager.getPlayers().stream().filter(_nickname -> Bukkit.getPlayer(_nickname).isOp()).collect(Collectors.toList()));
            }
        }
    }

    public static Map<String, PlayerScoreboard> getScoreboardMap() {
        return scoreboardMap;
    }
}
