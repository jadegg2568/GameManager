package com.jadegg.gamemanager.command;

import com.jadegg.gamemanager.main.Main;
import com.jadegg.gamemanager.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameManagerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (!p.isOp()) {
                return true;
            }
        }
        if (args.length == 0) {
            printHelp(sender);
            return true;
        }

        switch (args[0]) {
            case "create":
                if (Main.getGameManager() == null) {
                    Main.createGameManager();
                    Utils.broadcastAll("§aGameManager successfully created!");
                } else {
                    sender.sendMessage("§cGameManager already exists.");
                }
                break;
            case "delete":
                if (Main.getGameManager() == null) {
                    sender.sendMessage("§cGameManager already deleted.");
                } else {
                    Main.getGameManager().delete();
                    Main.setGameManager(null);
                    Utils.broadcastAll("§aGameManager successfully deleted!");
                }
                break;
            case "toggle":
                if (Main.getGameManager() == null) {
                    sender.sendMessage("§cGameManager don't exists.");
                } else {
                    Main.getGameManager().setEnabled(!Main.getGameManager().isEnabled());
                    if (Main.getGameManager().isEnabled())
                        sender.sendMessage("§eGameManager successfully §aenabled.");
                    else
                        sender.sendMessage("§eGameManager successfully §cdisabled.");
                }
                break;
            case "setLocation":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou must be a player to execute this command.");
                    return true;
                }
                Player p = (Player)sender;
                if (args.length != 2) {
                    printHelp(sender);
                    return true;
                }
                if (Main.getGameManager() == null) {
                    Main.createGameManager();
                    Utils.broadcastAll("§aGameManager не существует!");
                } else {
                    Main.getGameManager().getLocationMap().put(args[1], p.getLocation());
                    Utils.broadcastAll("§aУспешно установлена точка " + args[1] + ".");
                }
                break;
            default:
                printHelp(sender);
                break;
        }
        return true;
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage("§e/gamemanager create§f - create gameManager (uses config.yml properties).");
        sender.sendMessage("§e/gamemanager delete§f - delete gameManager.");
        sender.sendMessage("§e/gamemanager toggle§f - toggle gameManager.");
        sender.sendMessage("§e/gamemanager setLocation <name>§f - set game location (saved into location.json).");
    }
}
