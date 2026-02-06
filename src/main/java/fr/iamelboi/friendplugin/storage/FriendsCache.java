package fr.iamelboi.friendplugin.storage;

import fr.iamelboi.friendplugin.friends.FriendRelation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FriendsCache {
    private Map<UUID, FriendRelation> friendsMap = new HashMap<UUID, FriendRelation>();

    public FriendRelation getRelationByUUID(UUID uuid) {
        return friendsMap.get(uuid);
    }

    public void addInCache(FriendRelation relation) {
        friendsMap.put(relation.getUuid(), relation);
    }




}
