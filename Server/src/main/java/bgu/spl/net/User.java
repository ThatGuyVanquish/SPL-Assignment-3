package bgu.spl.net;

import java.util.Vector;

public class User {
    
    private int userId;
    private String username;
    private String password;
    private Vector<User> followingList;
    private Vector<User> followersList;
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
     * ^ meaning a user should exist before calling this
     */
    public void follow(User username) {
        if (this.followingList.indexOf(username) == -1){
            this.followingList.add(username);
            username.followedBy(this);
            // send ack message of successfull follow
        }
        else {
            // send error message of unsuccessfull follow
        }
        
    }

    /**
     * Removes a user to this user's following list
     * If user doesn't exist in the following list it should do nothing
     * 
     * @param username username of the person to follow
     * @pre ConnectionsImpl.clientMap.includes(username)
     * ^ meaning a user should exist before calling this
     *
     */
    public void unfollow(User username) {
        if (this.followersList.indexOf(username) != -1) {
            this.followingList.remove(username);
            username.unfollowedBy(this);
            // send ack message of successfull unfollow
        }
        else {
            // send error message of unsuccessfull unfollow
        }
    }

    public void followedBy(User username) {
        this.followersList.add(username);
    }

    public void unfollowedBy(User username) {
        this.followersList.remove(username);
    }

}
