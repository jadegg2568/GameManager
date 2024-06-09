package com.jadegg.gamemanager.util.thread;

import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.manage.SettingManager;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdown extends BukkitRunnable {

    private int time = -1;

    private GameManager gameManager;

    public GameCountdown(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        switch (time) {
            case 0:
                gameManager.startGame();
                break;
            case 60:
            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                gameManager.sendMessage("§eСтарт через §6" + time + " §eсек.");
                gameManager.playSound(Sound.BLOCK_NOTE_HAT, 1, 1);
                if (0 < time && time <= 5) {
                    gameManager.sendTitle("§c" + time, "§eСтарт через");
                }
                break;
        }

        if (time < 0) {
            try {
                throw new Exception("Unexpected continuation of GameCountdown!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        time--;
    }

    public void start() {
        time = SettingManager.countdownTime;
        this.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) { this.time = time; }
}
