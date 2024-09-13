package de.linkum.simeon.oneblockplugin;

import java.util.ArrayList;
import java.util.List;

public class FriendSystem {
    private List<String> friends = new ArrayList<>();
    public String friendName;
    public FriendSystem(String playerName) {
        this.friendName = playerName;
    }


    public void addFriend(String friendName) {
        this.friends.add(friendName);
    }

    public void removeFriend(String friendName) {
        this.friends.remove(friendName);
    }

    public List<String> getFriends() {
        return this.friends;
    }
}
