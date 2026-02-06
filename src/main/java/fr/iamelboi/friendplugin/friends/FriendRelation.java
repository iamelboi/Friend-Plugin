package fr.iamelboi.friendplugin.friends;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendRelation {
    private UUID uuid;
    private String username;
    private List<UUID> friendList = new ArrayList<UUID>();
    private List<UUID> waitingRequests = new ArrayList<UUID>();

    public FriendRelation(UUID uuid, String username) {
        uuid = uuid;
        username = username;
    }

    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }

    public List<UUID> getFriendList() { return friendList; }
    public void addFriend(UUID uuid){ friendList.add(uuid); }
    public void removeFriend(UUID uuid){ friendList.remove(uuid); }

    public List<UUID> getWaitingRequests() { return waitingRequests; }
    public void addWaitingRequest(UUID uuid){ waitingRequests.add(uuid); }
    public void removeWaitingRequest(UUID uuid){ waitingRequests.remove(uuid); }

    public boolean isFriend(UUID uuid){
        for (UUID friendUUID : friendList){
            if (friendUUID.equals(uuid)){
                return true;
            }
        }
        return false;
    }
}
