package fr.iamelboi.friendplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;



            if (cmd.getName().equalsIgnoreCase("test")){
                sender.sendMessage("§aVous avez executé la §ccommande /test");
                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("alert")){
                if (args.length == 0){
                    player.sendMessage("§6Friend plugin >§c Mauvaise utilisation de la commande :§r /alert <message>");
                }
                else if (args.length >= 1){
                    StringBuilder bc  = new StringBuilder();
                    for (String part : args){
                        bc.append(part + " ");
                    }

                    Bukkit.broadcastMessage("[" + player.getName() + "] " + bc.toString());
                }

                return  true;
            }

        }
        return false;
    }
}
