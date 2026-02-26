package fr.iamelboi.friendplugin.commands;

import fr.iamelboi.friendplugin.storage.FriendsCache;
import fr.iamelboi.friendplugin.storage.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandFriends implements CommandExecutor {

    private FriendsCache friendsCache = new FriendsCache();
    private PlayerCache playerCache = new PlayerCache();

    private boolean friendAdd(Player sender, Command cmd, String msg, String[] args) {

        if (args.length < 1) {
            return false;
        }

        String username = args[0];
        System.out.println("Target username: " + username);

        //Check si le joueur demandé existe
        UUID uuidTarget = playerCache.getUUIDByUsername(username);
        if (uuidTarget == null) {
            sender.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
            return true;
        }

        //Check if the player is dumb as fuck
        if (uuidTarget.equals(sender.getUniqueId())) {
            sender.sendMessage("§6Friend plugin >§c Vous ne pouvez pas vous ajouter vous-même en ami !");
            return true;
        }

        //Check si le joueur l'a deja dans sa liste d'ami
        if (friendsCache.getRelationByUUID(sender.getUniqueId()).isFriend(uuidTarget)) {
            sender.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c est déjà dans votre liste d'amis !");
            return true;
        }

        //Check si le joueur a deja envoyé une demande d'ami au joueur ciblé
        if (friendsCache.getRelationByUUID(sender.getUniqueId()).getWaitingRequests().contains(uuidTarget)) {
            sender.sendMessage("§6Friend plugin >§c Vous avez déjà envoyé une demande d'ami à §r" + username + "§c !");
            return true;
        }

        //Check si le joueur a deja une demande d'ami en attente de la part du joueur ciblé
        if (friendsCache.getRelationByUUID(uuidTarget).getWaitingRequests().contains(sender.getUniqueId())) {
            sender.sendMessage("§6Friend plugin >§c Vous avez déjà une demande d'ami en attente de la part de §r" + username + "§c !");
            return true;
        }

        friendsCache.getRelationByUUID(sender.getUniqueId()).addFriend(uuidTarget);
        return true;
    }

    private boolean friendRemove(Player sender, Command cmd, String msg, String[] args) {

        if (args.length < 1) {
            return false;
        }

        String username = args[0];
        System.out.println("Target username: " + username);

        //Check si le joueur choisi existe
        UUID uuidTarget = playerCache.getUUIDByUsername(username);
        if (uuidTarget == null) {
            sender.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
            return true;
        }

        //Check si le joueur l'a dans sa liste d'ami
        if (friendsCache.getRelationByUUID(sender.getUniqueId()).isFriend(uuidTarget)) {
            friendsCache.getRelationByUUID(sender.getUniqueId()).removeFriend(uuidTarget);
            return true;
        } else {
            sender.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'est pas dans votre liste d'amis !");
            return true;
        }
    }

    private boolean seeReceive(Player sender, Command cmd, String msg, String[] args) {
        List<UUID> receiveList = friendsCache.getRelationByUUID(sender.getUniqueId()).getWaitingRequests();
        if (receiveList.isEmpty()) {
            sender.sendMessage("§6Friend plugin >§c Vous n'avez aucune demande d'ami en attente !");
        } else {
            sender.sendMessage("§6Friend plugin >§r Demandes d'ami en attente :");
            for (UUID uuid : receiveList) {
                String username = playerCache.getUsernameByUUID(uuid);
                sender.sendMessage("- " + username);
            }
        }
        return true;
    }

    private boolean seeAllList(Player sender, Command cmd, String msg, String[] args) {
        List<UUID> friendList = friendsCache.getRelationByUUID(sender.getUniqueId()).getFriendList();
        if (friendList.isEmpty()){
            sender.sendMessage("§6Friend Plugin >§c Votre liste d'ami est vide");
            return true;
        }
        else {
            sender.sendMessage("§6Friend Plugin >§r Liste d'ami :");
            for (UUID uuid : friendList){
                String username = playerCache.getUsernameByUUID(uuid);
                sender.sendMessage("- " + username);
            }
        }
        return true;
    }

    private boolean deleteAllFriend(Player sender, Command cmd, String msg, String[] args) {
        friendsCache.getRelationByUUID(sender.getUniqueId()).getFriendList().clear();
        sender.sendMessage("§6FriendPlugin >§a Votre liste d'ami a été entièrement supprimée.");
        return true;
    }

    private boolean seeAllConnected(Player sender, Command cmd, String msg, String[] args) {
        List<UUID> friendList = friendsCache.getRelationByUUID(sender.getUniqueId()).getFriendList();
        if (friendList.isEmpty()){
            sender.sendMessage("§6Friend Plugin >§c Votre liste d'ami est vide");
        }
        else {
            List<UUID> friendConnected = new ArrayList<>();
            for (UUID uuid : friendList){
                if (Bukkit.getPlayer(uuid) != null){
                    friendConnected.add(uuid);
                }
            }

            if (friendConnected.isEmpty()){
                sender.sendMessage("§6Friend Plugin >§c Vous n'avez aucun ami conneté");
            }
            else {
                sender.sendMessage("§6Friend Plugin >§r Liste d'ami connecté:");
                for (UUID uuid : friendConnected){
                    String username = playerCache.getUsernameByUUID(uuid);
                    sender.sendMessage("- " + username);
                }
            }
        }
        return true;
    }

    private boolean friendAccept(Player sender, Command cmd, String msg, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String username = args[0];
        UUID uuidTarget = playerCache.getUUIDByUsername(username);
        if (uuidTarget == null) {
            sender.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
        } else if (friendsCache.getRelationByUUID(sender.getUniqueId()).getWaitingRequests().contains(uuidTarget)) {
            friendsCache.getRelationByUUID(sender.getUniqueId()).addFriend(uuidTarget);
            friendsCache.getRelationByUUID(sender.getUniqueId()).getWaitingRequests().remove(uuidTarget);
            friendsCache.getRelationByUUID(uuidTarget).addFriend(sender.getUniqueId());
            sender.sendMessage("§6Friend plugin >§a Vous avez accepté la demande d'ami de §r" + username + "§a !");
            if (Bukkit.getPlayer(uuidTarget) != null) {
                Player targetPlayer = Bukkit.getPlayer(uuidTarget);
                targetPlayer.sendMessage("§6Friend plugin >§a Votre demande d'ami a été acceptée par §r" + sender.getName() + "§a !");
            }
        } else {
            sender.sendMessage("§6Friend plugin >§c Vous n'avez pas de demande d'ami en attente de la part de §r" + username + "§c !");
        }

        return true;
    }

    private boolean friendDeny(Player sender, Command cmd, String msg, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String username = args[0];
        UUID uuidTarget = playerCache.getUUIDByUsername(username);
        if (uuidTarget == null) {
            sender.sendMessage("§6Friend plugin >§c Le joueur §r" + username + "§c n'existe pas !");
        } else if (friendsCache.getRelationByUUID(sender.getUniqueId()).getWaitingRequests().contains(uuidTarget)) {
            friendsCache.getRelationByUUID(sender.getUniqueId()).getWaitingRequests().remove(uuidTarget);
            sender.sendMessage("§6Friend plugin >§a Vous avez refusé la demande d'ami de §r" + username + "§a !");
            if (Bukkit.getPlayer(uuidTarget) != null) {
                Player targetPlayer = Bukkit.getPlayer(uuidTarget);
                targetPlayer.sendMessage("§6Friend plugin >§c Votre demande d'ami a été refusée par §r" + sender.getName() + "§c !");
            }
        } else {
            sender.sendMessage("§6Friend plugin >§c Vous n'avez pas de demande d'ami en attente de la part de §r" + username + "§c !");
        }

        return true;
    }

    @FunctionalInterface
    interface CommandExecutorFunction {
        boolean execute(Player sender, Command cmd, String msg, String[] args);
    }

    private Map<String, CommandExecutorFunction> commandMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            System.out.println(sender.getName() + " execute " + cmd.getName());

            CommandExecutorFunction executor = commandMap.get(cmd.getName());
            if (executor != null) {
                return executor.execute(player, cmd, msg, args);
            }
        }
        
        return false;
    }
}
