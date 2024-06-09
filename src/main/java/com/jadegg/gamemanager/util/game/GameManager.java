package com.jadegg.gamemanager.util.game;

import com.jadegg.gamemanager.manage.MechanicManager;
import com.jadegg.gamemanager.manage.PlayerManager;
import com.jadegg.gamemanager.manage.ScoreboardManager;
import com.jadegg.gamemanager.manage.TeamManager;
import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.manage.SettingManager;
import com.jadegg.gamemanager.util.thread.GameCountdown;
import com.jadegg.gamemanager.util.thread.GameRunnable;
import com.jadegg.gamemanager.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private final String name;
    private int minimum;
    private int maximum;
    private int teamCount;
    private int playersPerTeam;
    private boolean isEnabled;
    private Map<String, Location> locationMap;

    private List<String> players;
    private List<String> spectators;
    private Map<String, GameTeam> gameTeams;

    private GameCountdown gameCountdown;
    private GameRunnable gameRunnable;
    private GameState gameState;

    public GameManager(String name, int minimum, int maximum, int teamCount, int playersPerTeam, boolean instantStart) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.teamCount = teamCount;
        this.playersPerTeam = playersPerTeam;
        this.isEnabled = SettingManager.isEnabled;
        locationMap = new HashMap<>();

        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.gameTeams = new HashMap<>();

        this.gameCountdown = new GameCountdown(this);
        this.gameRunnable = new GameRunnable(this);

        this.gameState = GameState.WAITING;

        if (teamCount != 2 && teamCount != 4 /*&& teamCount != 8*/
                || maximum != (teamCount * playersPerTeam)
                || minimum != (playersPerTeam * 2)) {
            Main.getInstance().getLogger().warning("Invalid game properties. GameManager deleting.");
            Main.setGameManager(null);
            return;
        }

        if (instantStart && isEnabled) {
            Bukkit.getOnlinePlayers().forEach((p) -> addPlayer(p.getName()));
        }
    }

    public void sendMessage(String message) {
        Bukkit.getServer().getLogger().info(message);
        players.stream().map(Bukkit::getPlayer).forEach(p -> p.sendMessage(message));
        spectators.stream().map(Bukkit::getPlayer).forEach(p -> p.sendMessage(message));
    }

    public void sendTitle(String title, String subtitle) {
        players.stream().map(Bukkit::getPlayer).forEach(p -> p.sendTitle(title, subtitle));
        spectators.stream().map(Bukkit::getPlayer).forEach(p -> p.sendTitle(title, subtitle));
    }

    public void playSound(Sound sound, float v1, float v2) {
        players.stream().map(Bukkit::getPlayer).forEach(p -> p.playSound(p.getLocation(), sound, v1, v2));
        spectators.stream().map(Bukkit::getPlayer).forEach(p -> p.playSound(p.getLocation(), sound, v1, v2));
    }

    public void addPlayer(String nickname) {
        Player p = Bukkit.getPlayer(nickname);
        switch (gameState) {
            case WAITING:
            case STARTING:
                if (!players.contains(nickname)) players.add(nickname);
                if (locationMap.containsKey("waitingSpawn")) p.teleport(locationMap.get("waitingSpawn"));
                PlayerManager.reset(p);
                PlayerManager.giveSelectInventory(p);
                ScoreboardManager.createWaitingObjective(this, nickname);

                sendMessage("§7" + nickname + " §eприсоединился.");

                if (players.size() == minimum) {
                    gameState = GameState.STARTING;
                    sendMessage("§eСтарт обратного отсчёта...");
                    gameCountdown.start();

                    ScoreboardManager.createWaitingObjective(this);
                }
                if (players.size() == maximum && gameCountdown.getTime() > 15) {
                    gameCountdown.setTime(15);
                }
                break;
            case GAME:
                p.kickPlayer("§cАрена в игре!");
                break;
            case END:
                p.kickPlayer("§cАрена перезагружается!");
                break;
        }
    }

    public void removePlayer(String nickname) {
        if (!players.contains(nickname)) return;
        Player p = Bukkit.getPlayer(nickname);

        players.remove(nickname);
        PlayerManager.reset(p);
        PlayerManager.clearInventory(p);

        switch (gameState) {
            case WAITING:
            case STARTING:
                sendMessage("§7" + nickname + " §eпокинул игру.");

                if (players.size() == (minimum - 1)) {
                    gameState = GameState.WAITING;
                    sendMessage("§eОстановка обратного отсчёта...");
                    gameCountdown.cancel();
                    gameCountdown = new GameCountdown(this);

                    ScoreboardManager.createWaitingObjective(this);
                }
                break;
            case GAME:
                sendMessage("§7" + TeamUtils.getColorCode(getGameTeam(nickname).getName()) + nickname + " §7отключился.");
                GameTeam gameTeam = getGameTeam(nickname);
                gameTeam.remove(nickname);

                if (TeamManager.aliveTeamCount(this) == 1)
                    endGame(gameTeams.values().stream().filter(GameTeam::isAlive).findFirst().get());
                break;
            case END:
                break;
        }
    }

    public void startGame() {
        if (gameState == GameState.GAME) return;
        gameState = GameState.GAME;
        sendMessage("§aУдачной игры!");
        sendTitle("§aУдачной игры!", "");

        playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        MechanicManager.startGameMechanics(this);
        ScoreboardManager.reset(this);
        ScoreboardManager.setupPrefixTeams(this);
        ScoreboardManager.createGameObjective(this);

        gameCountdown.cancel();
        gameCountdown = new GameCountdown(this);
        gameRunnable.start();
    }

    public void endGame(GameTeam winnerTeam) {
        if (gameState == GameState.END) return;
        gameState = GameState.END;

        sendMessage("§8----------------------------");
        if (winnerTeam == null) {
            sendMessage("   §7Победитель: §6НИЧЬЯ");
        } else {
            sendMessage("   §7Победитель: §" + TeamUtils.getColorCode(winnerTeam.getName()) + winnerTeam.getName());
            for (String nickname : winnerTeam.getPlayers())
                Bukkit.getPlayer(nickname).sendTitle("§6ПОБЕДА", "");
        }
        sendMessage("§8----------------------------");
        playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        gameRunnable.cancel();
        gameRunnable = new GameRunnable(this);

        MechanicManager.endGameMechanics(this);
        ScoreboardManager.setupPrefixTeams(this);

        sendMessage("§cПерезагрузка через 10 секунд!");
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::reload, 200L);
    }

    public void reload() {
        List<String> totalPlayers = new ArrayList<>(players);
        ScoreboardManager.reset(this);
        players.clear();

        gameState = GameState.WAITING;
        totalPlayers.forEach(this::addPlayer);

        Main.getInstance().getLogger().info("Game-arena has been reloaded.");
    }

    public void addSpectator(String nickname, String message) {
        if (players.contains(nickname)) players.remove(nickname);
        spectators.add(nickname);

        Player p = Bukkit.getPlayer(nickname);
        p.setGameMode(GameMode.SPECTATOR);
        p.sendMessage(message);
    }

    public void removeSpectator(String nickname, boolean addBackToPlayers) {
        if (addBackToPlayers) players.add(nickname);
        spectators.remove(nickname);

        Player p = Bukkit.getPlayer(nickname);
        p.setGameMode(GameMode.SURVIVAL);
    }

    public GameTeam getGameTeam(String nickname) {
        for (GameTeam gameTeam : gameTeams.values()) {
            if (gameTeam.getPlayers().contains(nickname)) return gameTeam;
        }
        return null;
    }

    public void delete() {
        for (String nickname : players)
            ScoreboardManager.getScoreboard(nickname).reset();
        try { gameCountdown.cancel(); } catch (Exception ignored) { }
        try { gameRunnable.cancel(); } catch (Exception ignored) { }
    }

    public String getName() {
        return name;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public Map<String, Location> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, Location> locationMap) {
        this.locationMap = locationMap;
    }

    public GameCountdown getGameCountdown() {
        return gameCountdown;
    }

    public void setGameCountdown(GameCountdown gameCountdown) {
        this.gameCountdown = gameCountdown;
    }

    public GameRunnable getGameRunnable() {
        return gameRunnable;
    }

    public void setGameRunnable(GameRunnable gameRunnable) {
        this.gameRunnable = gameRunnable;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public List<String> getSpectators() {
        return spectators;
    }

    public void setSpectators(List<String> spectators) {
        this.spectators = spectators;
    }

    public Map<String, GameTeam> getGameTeams() {
        return gameTeams;
    }

    public void setGameTeams(Map<String, GameTeam> gameTeams) { this.gameTeams = gameTeams; }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public int getPlayersPerTeam() {
        return playersPerTeam;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }
}
