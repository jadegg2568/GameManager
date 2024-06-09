package com.jadegg.gamemanager.util.thread;

import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.util.game.GameState;
import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.manage.TeamManager;
import com.jadegg.gamemanager.utils.Utils;
import com.jadegg.gamemanager.util.PlayerScoreboard;
import com.jadegg.gamemanager.manage.ScoreboardManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {

    @Override
    public void run() {
        GameManager gameManager = Main.getGameManager();
        if (gameManager == null) return;
        GameState gameState = gameManager.getGameState();

        for (String nickname : ScoreboardManager.getScoreboardMap().keySet()) {
            PlayerScoreboard playerScoreboard = ScoreboardManager.getScoreboard(nickname);

            if (playerScoreboard.hasObjective("waitingObjective")) {
                playerScoreboard.changeSuffix("players", "§a" + gameManager.getPlayers().size() + "/" + gameManager.getMaximum());
                playerScoreboard.changeSuffix("map", "§aNONE");
                if (gameState == GameState.STARTING)
                    playerScoreboard.changeSuffix("time", "§a" + Utils.toMMSS(gameManager.getGameCountdown().getTime()));

            } else if (playerScoreboard.hasObjective("gameObjective") && gameState == GameState.GAME) {
                playerScoreboard.changeSuffix("players", "§a" + gameManager.getPlayers().size());
                playerScoreboard.changeSuffix("aliveTeams", "§a" + TeamManager.aliveTeamCount(gameManager));
                playerScoreboard.changeSuffix("time", "§a" + Utils.toMMSS(gameManager.getGameRunnable().getTime()));
            }
        }
    }

    public void start() {
        this.runTaskTimer(Main.getInstance(), 0L, 10L);
    }
}
