package com.jadegg.gamemanager.manage;

import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.util.game.GameTeam;
import com.jadegg.gamemanager.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MechanicManager {

    public static void startGameMechanics(GameManager gameManager) {
        TeamManager.distributeIntoTeams(gameManager);

        for (GameTeam gameTeam : gameManager.getGameTeams().values()) {
            String name = gameTeam.getName();
            List<String> players = gameTeam.getPlayers();

            gameTeam.teleportToSpawn();

            for (String nickname : players) {
                PlayerManager.giveGameInventory(Bukkit.getPlayer(nickname));
            }
        }

        SettingManager.pvpEnabled = true;
        SettingManager.gunsEnabled = true;
    }

    public static void endGameMechanics(GameManager gameManager) {
        SettingManager.pvpEnabled = false;
        SettingManager.gunsEnabled = false;

        for (String spectator : new ArrayList<>(gameManager.getSpectators())) {
            Player p = Bukkit.getPlayer(spectator);
            p.setGameMode(GameMode.SURVIVAL);
            gameManager.getPlayers().add(spectator);
            gameManager.getSpectators().remove(spectator);
        }

        for (String nickname : gameManager.getPlayers()) {
            Player p = Bukkit.getPlayer(nickname);
            PlayerManager.giveSelectInventory(p);
        }
        gameManager.getGameTeams().clear();
    }

    public static void playerDied(GameManager gameManager, String nickname, String killer) {
        if (!gameManager.getPlayers().contains(nickname)) return;

        GameTeam gameTeam = gameManager.getGameTeam(nickname);

        if (killer == null) {
            gameManager.sendMessage("§" + TeamUtils.getColorCode(gameTeam.getName()) + nickname + " §7выбыл.");
        } else {
            GameTeam killerTeam = gameManager.getGameTeam(killer);
            gameManager.sendMessage("§" + TeamUtils.getColorCode(gameTeam.getName()) + nickname + " §7был убит игроком §" + TeamUtils.getColorCode(killerTeam.getName()) + killer + "§7.");
        }

        gameTeam.remove(nickname);

        if (TeamManager.aliveTeamCount(gameManager) > 1) {
            gameManager.getPlayers().remove(nickname);
            gameManager.getSpectators().add(nickname);
            Player p = Bukkit.getPlayer(nickname);
            p.setGameMode(GameMode.SPECTATOR);

            p.sendMessage("§eВы умерли. Чтобы выйти, пропишите /leave");
            for (String pl : gameManager.getPlayers())
                ScoreboardManager.getScoreboard(pl).changeTeam("spectators", nickname);
            for (String pl : gameManager.getSpectators())
                ScoreboardManager.getScoreboard(pl).changeTeam("spectators", nickname);
        } else {
            Player p = Bukkit.getPlayer(nickname);
            p.getWorld().strikeLightning(p.getLocation());
            Optional<GameTeam> winnerTeamOptional = gameManager.getGameTeams()
                    .values()
                    .stream()
                    .filter(GameTeam::isAlive)
                    .findFirst();
            if (winnerTeamOptional.isPresent()) {
                gameManager.endGame(winnerTeamOptional.get());
            } else {
                gameManager.endGame(null);
            }
        }
    }
}
