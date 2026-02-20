package fr.iamelboi.friendplugin.commands;

import fr.iamelboi.friendplugin.storage.FriendsCache;
import fr.iamelboi.friendplugin.storage.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandFriends implements CommandExecutor {

    private FriendsCache friendsCache = new FriendsCache();
    private PlayerCache playerCache = new PlayerCache();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("friend-add")) {
                System.out.println(player.getName() + " executed /friend-add");
                String username = args[0];
                System.out.println("Target username: " + username);

                //Check si le joueur demandé existe
                UUID uuidTarget = playerCache.getUUIDByUsername(username);
                if (uuidTarget == null) {
                    player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
                    return true;
                }

                //Check if the player is dumb as fuck
                if (uuidTarget.equals(player.getUniqueId())) {
                    player.sendMessage("§6Friend plugin >§c Vous ne pouvez pas vous ajouter vous-même en ami !");
                    return true;
                }

                //Check si le joueur l'a deja dans sa liste d'ami
                if (friendsCache.getRelationByUUID(player.getUniqueId()).isFriend(uuidTarget)) {
                    player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c est déjà dans votre liste d'amis !");
                    return true;
                }
                ;

                //Check si le joueur a deja envoyé une demande d'ami au joueur ciblé
                if (friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests().contains(uuidTarget)) {
                    player.sendMessage("§6Friend plugin >§c Vous avez déjà envoyé une demande d'ami à §r" + username + "§c !");
                    return true;
                }

                //Check si le joueur a deja une demande d'ami en attente de la part du joueur ciblé
                if (friendsCache.getRelationByUUID(uuidTarget).getWaitingRequests().contains(player.getUniqueId())) {
                    player.sendMessage("§6Friend plugin >§c Vous avez déjà une demande d'ami en attente de la part de §r" + username + "§c !");
                    return true;
                }

                friendsCache.getRelationByUUID(player.getUniqueId()).addFriend(uuidTarget);
                return true;
            } else if (cmd.getName().equalsIgnoreCase("friend-remove")) {
                System.out.println(player.getName() + " executed /friend-remove");
                String username = args[0];
                System.out.println("Target username: " + username);

                //Check si le joueur choisi existe
                UUID uuidTarget = playerCache.getUUIDByUsername(username);
                if (uuidTarget == null) {
                    player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
                    return true;
                }

                //Check si le joueur l'a dans sa liste d'ami
                if (friendsCache.getRelationByUUID(player.getUniqueId()).isFriend(uuidTarget)) {
                    friendsCache.getRelationByUUID(player.getUniqueId()).removeFriend(uuidTarget);
                    return true;
                } else {
                    player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'est pas dans votre liste d'amis !");
                    return true;
                }
            } else if (cmd.getName().equalsIgnoreCase("seeReceive")) {
                System.out.println(player.getName() + " executed /seeReceive");
                List<UUID> receiveList = friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests();
                if (receiveList.isEmpty()) {
                    player.sendMessage("§6Friend plugin >§c Vous n'avez aucune demande d'ami en attente !");
                } else {
                    player.sendMessage("§6Friend plugin >§r Demandes d'ami en attente :");
                    for (UUID uuid : receiveList) {
                        String username = playerCache.getUsernameByUUID(uuid);
                        player.sendMessage("- " + username);
                    }
                }
                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("seeAllList")){
                System.out.println(player.getName() + " executed /seeAllList");
                List<UUID> friendList = friendsCache.getRelationByUUID(player.getUniqueId()).getFriendList();
                if (friendList.isEmpty()){
                    player.sendMessage("§6Friend Plugin >§c Votre liste d'ami est vide");
                    return true;
                }
                else {
                    player.sendMessage("§6Friend Plugin >§r Liste d'ami :");
                    for (UUID uuid : friendList){
                        String username = playerCache.getUsernameByUUID(uuid);
                        player.sendMessage("- " + username);
                    }
                }
            }

            else if (cmd.getName().equalsIgnoreCase("deleteAllFriend")){
                System.out.println(player.getName() + " executed /deleteAllFriend");
                friendsCache.getRelationByUUID(player.getUniqueId()).getFriendList().clear();
                player.sendMessage("§6FriendPlugin >§a Votre liste d'ami a été entièrement supprimée.");
                return true;
            }

            else if (cmd.getName().equalsIgnoreCase("seeAllConnected")){
                System.out.println(player.getName() + " executed /seeAllConnected");
                List<UUID> friendList = friendsCache.getRelationByUUID(player.getUniqueId()).getFriendList();
                if (friendList.isEmpty()){
                    player.sendMessage("§6Friend Plugin >§c Votre liste d'ami est vide");
                }
                else {
                    List<UUID> friendConnected = new ArrayList<>();
                    for (UUID uuid : friendList){
                        if (Bukkit.getPlayer(uuid) != null){
                            friendConnected.add(uuid);
                        }
                    }

                    if (friendConnected.isEmpty()){
                        player.sendMessage("§6Friend Plugin >§c Vous n'avez aucun ami conneté");
                    }
                    else {
                        player.sendMessage("§6Friend Plugin >§r Liste d'ami connecté:");
                        for (UUID uuid : friendConnected){
                            String username = playerCache.getUsernameByUUID(uuid);
                            player.sendMessage("- " + username);
                        }
                    }
                }
                return true;
            }

            else if(cmd.getName().equalsIgnoreCase("friend-accept")){
                System.out.println(player.getName() + " execute /friend-accept");
                if (args.length < 2){
                    player.sendMessage("§cUsage : /friend-accept <username>");
                }
                else{
                    String username = args[0];
                    UUID uuidTarget = playerCache.getUUIDByUsername(username);
                    if (uuidTarget == null) {
                        player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
                    }
                    else if (friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests().contains(uuidTarget)){
                        friendsCache.getRelationByUUID(player.getUniqueId()).addFriend(uuidTarget);
                        friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests().remove(uuidTarget);
                        friendsCache.getRelationByUUID(uuidTarget).addFriend(player.getUniqueId());
                        player.sendMessage("§6Friend plugin >§a Vous avez accepté la demande d'ami de §r" + username + "§a !");
                        if (Bukkit.getPlayer(uuidTarget) != null){
                            Player targetPlayer = Bukkit.getPlayer(uuidTarget);
                            targetPlayer.sendMessage("§6Friend plugin >§a Votre demande d'ami a été acceptée par §r" + player.getName() + "§a !");
                        }
                    }
                    else {
                        player.sendMessage("§6Friend plugin >§c Vous n'avez pas de demande d'ami en attente de la part de §r" + username + "§c !");
                    }
                }
                return true;
            }

            else if (cmd.getName().equalsIgnoreCase("friend-deny")) {
                System.out.println(player.getName() + " execute /friend-deny");
                if (args.length < 2) {
                    player.sendMessage("§cUsage : /friend-deny <username>");
                } else {
                    String username = args[0];
                    UUID uuidTarget = playerCache.getUUIDByUsername(username);
                    if (uuidTarget == null) {
                        player.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
                    } else if (friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests().contains(uuidTarget)) {
                        friendsCache.getRelationByUUID(player.getUniqueId()).getWaitingRequests().remove(uuidTarget);
                        player.sendMessage("§6Friend plugin >§a Vous avez refusé la demande d'ami de §r" + username + "§a !");
                        if (Bukkit.getPlayer(uuidTarget) != null) {
                            Player targetPlayer = Bukkit.getPlayer(uuidTarget);
                            targetPlayer.sendMessage("§6Friend plugin >§c Votre demande d'ami a été refusée par §r" + player.getName() + "§c !");
                        }
                    } else {
                        player.sendMessage("§6Friend plugin >§c Vous n'avez pas de demande d'ami en attente de la part de §r" + username + "§c !");
                    }
                }
                return true;
            }
        }



        return false;
    }
}
