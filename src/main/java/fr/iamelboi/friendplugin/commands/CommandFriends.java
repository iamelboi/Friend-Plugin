package fr.iamelboi.friendplugin.commands;

import fr.iamelboi.friendplugin.storage.FriendsCache;
import fr.iamelboi.friendplugin.storage.PlayerCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFriends implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        FriendsCache friendsCache = new FriendsCache();
        PlayerCache playerCache = new PlayerCache();

        if (sender instanceof Player){
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("friend-add")){
                System.out.println(player.getName() + " executed /friend-add");
                String username = args[0];
                System.out.println("Target username: " + username);

                //Check si le joueur demandé existe
                UUID uuidTarget = playerCache.getUUIDByUsername(username);
                if (uuidTarget == null){
                    player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
                    return true;
                }

                //Check if the player is dumb as fuck
                if (uuidTarget.equals(player.getUniqueId())){
                    player.sendMessage("§6Friend plugin >§c Vous ne pouvez pas vous ajouter vous-même en ami !");
                    return true;
                }

                //Check si le joueur l'a deja dans sa liste d'ami
                if (friendsCache.getRelationByUUID(player.getUniqueId()).isFriend(uuidTarget)){
                    player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c est déjà dans votre liste d'amis !");
                    return true;
                };

                //Check si le joueur a deja envoyé une demande d'ami au joueur ciblé
                if (friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests().contains(uuidTarget)){
                    player.sendMessage("§6Friend plugin >§c Vous avez déjà envoyé une demande d'ami à §r" + username + "§c !");
                    return true;
                }

                //Check si le joueur a deja une demande d'ami en attente de la part du joueur ciblé
                if (friendsCache.getRelationByUUID(uuidTarget).getWaitingRequests().contains(player.getUniqueId())){
                    player.sendMessage("§6Friend plugin >§c Vous avez déjà une demande d'ami en attente de la part de §r" + username + "§c !");
                    return true;
                }

                friendsCache.getRelationByUUID(player.getUniqueId()).addFriend(uuidTarget);
                return true;
            }
        }
        return false;
    }
}
