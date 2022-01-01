package bgu.spl.net;

import java.sql.Time;
import java.util.Vector;

public class User {
    
    private String username;
    private String password;
    private String birthday;
    private Vector<User> followingList;
    private Vector<User> followersList;
    private Vector<User> blockList;
    private boolean loginStatus;
    private int numOfPosts;
    
    public User( String username, String password, String birthday) {
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.followingList = new Vector<>();
        this.followersList = new Vector<>();
        this.blockList = new Vector<>();
        this.loginStatus = false;
        this.numOfPosts = 0;
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
    public void unfollow(User username, boolean blockReq) {
        if (this.followersList.indexOf(username) != -1) {
            this.followingList.remove(username);
            username.unfollowedBy(this);
            // send ack message of successfull unfollow if not blockReq
        }
        else {
            // send error message of unsuccessfull unfollow if not blockReq
        }
    }

    public void followedBy(User username) {
        this.followersList.add(username);
    }

    public void unfollowedBy(User username) {
        this.followersList.remove(username);
    }

    public void block(User username) {
        this.unfollow(username, true);
        username.unfollow(this, true);
        this.blockList.add(username);
        // send ack message to acknowledge successfull block
    }

    public double getAge() {
        int day = Integer.parseInt(this.birthday.substring(0,2));
        int month = Integer.parseInt(this.birthday.substring(3,5));
        int year = Integer.parseInt(this.birthday.substring(6));
        long millis = System.currentTimeMillis();  
        java.sql.Date date = new java.sql.Date(millis);
        double age = date.getYear() - year - (date.getMonth() - month)/12 - (date.getDay() - day)/365;
        return age;
    }

    public void post() { // Don't know if this should be more elaborate
        this.numOfPosts++;
    }

    public String getStats() {
        return this.getAge() + " " + this.numOfPosts + " " + this.followersList.size() + " " + this.followingList.size();
    }

}
