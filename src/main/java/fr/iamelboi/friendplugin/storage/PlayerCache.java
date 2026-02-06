package fr.iamelboi.friendplugin.storage;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;


public class PlayerCache {
    private Map<UUID, String> playerCache = new HashMap<UUID, String>();

    public String getUsernameByUUID(UUID uuid) {
        return playerCache.get(uuid);
    }

    public UUID getUUIDByUsername(String username) {
        for (Map.Entry<UUID, String> entry : playerCache.entrySet()) {
            if (entry.getValue() == username) {
                return entry.getKey();
            }
        }
        return null;
    }


}
