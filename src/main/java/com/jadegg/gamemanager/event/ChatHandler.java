package com.jadegg.gamemanager.event;

import com.jadegg.gamemanager.util.game.GameManager;
import com.jadegg.gamemanager.util.game.GameState;
import com.jadegg.gamemanager.util.game.GameTeam;
import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandler implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (Main.getGameManager() == null || !Main.getGameManager().isEnabled()) return;
        if (!Main.getGameManager().getPlayers().contains(event.getPlayer().getName())) return;
        event.setCancelled(true);
        Player p = event.getPlayer();
        String message = event.getMessage();

        GameManager gameManager = Main.getGameManager();
        if (gameManager.getGameState() == GameState.GAME) {
            GameTeam gameTeam = gameManager.getGameTeam(p.getName());
            if (message.startsWith("!")) {
                gameManager.sendMessage("§8[§" + TeamUtils.getColorCode(gameTeam.getName()) + "All§8] §7" + p.getName() + ": " + message);
            } else {
                for (String nickname : gameTeam.getPlayers()) {
                    Bukkit.getPlayer(nickname).sendMessage("§8[§" + TeamUtils.getColorCode(gameTeam.getName()) + "Team§8] §7" + nickname + ": " + message);
                }
            }
        } else {
            if (p.isOp()) {
                gameManager.sendMessage("§c[ADMIN] " + p.getName() + "§7: " + message);
            } else {
                gameManager.sendMessage("§7[Player] " + p.getName() + ": " + message);
            }
        }
    }
}
