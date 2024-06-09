package com.jadegg.gamemanager.util.thread;

import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.manage.SettingManager;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {

    private GameManager gameManager;
    private int time = SettingManager.gameTime;

    public GameRunnable(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        if (time == 0) {
            gameManager.endGame(null);
        }
        if (time < 0) {
            try {
                throw new Exception("Unexpected continuation of GameRunnable!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        time--;
    }

    public void start() {
        runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public int getTime() {
        return time;
    }
}
