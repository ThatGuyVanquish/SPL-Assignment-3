package bgu.spl.net;

import java.util.Vector;

public class User {
    
    private int userId;
    private String username;
    private String password;
    private Vector<String> followingList;
    private Vector<String> followersList;
    private boolean loginStatus;
    
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.followingList = new Vector<>();
        this.followersList = new Vector<>();
        this.loginStatus = false;
    }

    public void login(String username, String password) {
        if (username.equals(this.username) && password.equals(this.password)) loginStatus = true;
        // send an ACK message to acknowledge login success
    }

    public void logout() {
        // Shouldn't exist here as user is going to be deleted anyway, whenever it is called server should just 
        // delete the user from the hashmap in Connections 
    }

    /**
     * Adds a user to this user's following list
     * 
     * @param username username of the person to follow
     * @pre ConnectionsImpl.clientMap.includes(username)
     */
    public void follow(String username) {
        this.followingList.add(username);
    }

    public void unfollow(String username) {

    }

}
