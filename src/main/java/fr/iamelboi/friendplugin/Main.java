package fr.iamelboi.friendplugin;

import fr.iamelboi.friendplugin.commands.CommandFriends;
import fr.iamelboi.friendplugin.commands.CommandTest;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("[FriendPlugin]: Plugin enabled");
        getCommand("friend-add").setExecutor(new CommandFriends());
        getCommand("friend-remove").setExecutor(new CommandFriends());
        getCommand("seeReceive").setExecutor(new CommandFriends());
        getCommand("seeAllList").setExecutor(new CommandFriends());
        getCommand("deleteAllFriend").setExecutor(new CommandFriends());
        getCommand("friend-accept").setExecutor(new CommandFriends());
        getCommand("friend-deny").setExecutor(new CommandFriends());


        getCommand("test").setExecutor(new CommandTest());
        getCommand("alert").setExecutor(new CommandTest());
    }

    @Override
    public void onDisable() {
        System.out.println("[FriendPlugin]: Plugin disabled");
    }

}
