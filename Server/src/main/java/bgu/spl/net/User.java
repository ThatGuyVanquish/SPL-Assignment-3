package bgu.spl.net;

import java.util.HashMap;
import java.util.Vector;

public class User {
    
    private Database DATABASE = Database.getInstance();
    private String username;
    private String password;
    private String birthday;
    private Vector<User> followingList;
    private Vector<User> followersList;
    private Vector<User> blockList;
    private Vector<User> blockedBy;
    private boolean loginStatus;
    private Vector<String> posts;
    private HashMap<User, Vector<String>> pms;
    
    public User( String username, String password, String birthday) {
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.followingList = new Vector<>();
        this.followersList = new Vector<>();
        this.blockList = new Vector<>();
        this.blockedBy = new Vector<>();
        this.loginStatus = false;
        this.posts = new Vector<>();
        this.pms = new HashMap<>();
    }   

    public boolean login(String username, String password) {
        if (username.equals(this.username) && password.equals(this.password)) {
            this.loginStatus = true;
            return true;
        }
        return false;
    }

    public boolean logout() {
        if (this.isOnline()) {
            this.loginStatus = false;
            return true;
        }
        return false;
    }

    public boolean isOnline() {
        return this.loginStatus;
    }

    public String getUsername() {
        return this.username;
    }

    /**
     * Adds a user to this user's following list
     * 
     * @param username username of the person to follow
     */
    public boolean follow(User username) {
        if (!DATABASE.isRegistered(username.getUsername())) return false;
        if (this.followingList.indexOf(username) == -1){
            this.followingList.add(username);
            username.followedBy(this);
            return true;
        }
        return false;
    }

    /**
     * Removes a user to this user's following list
     * If user doesn't exist in the following list it should do nothing
     * 
     * @param username username of the person to follow
     */
    public boolean unfollow(User username) {
        if (!DATABASE.isRegistered(username.getUsername())) return false;
        if (this.followersList.indexOf(username) != -1) {
            this.followingList.remove(username);
            username.unfollowedBy(this);
            return true;
        }
        return false;
    }

    /**
     * Method to add a User to another User's Followers List
     * @param username user that followed @this
     * @pre DATABASE.isRegistered(this)
     */
    public void followedBy(User username) {
        this.followersList.add(username);
    }

    /**
     * Method to remove a User from another User's Followers List
     * @param username user that unfollowed @this
     * @pre DATABASE.isRegistered(this)
     */
    public void unfollowedBy(User username) {
        this.followersList.remove(username);
    }

    public boolean block(User username) {
        if (!DATABASE.isRegistered(username.getUsername())) return false;
        this.unfollow(username);
        username.unfollow(this);
        this.blockList.add(username);
        username.getBlocked(this);
        return true;
    }

    /**
     * Method to add a User to another User's Blockers List
     * @param username user that blocked @this
     * @pre DATABASE.isRegistered(this)
     */
    public void getBlocked(User username) {
        this.blockedBy.add(username);
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

    public boolean post(String post) {
        if (this.isOnline()) {
            this.posts.add(post);
            return true;
        }
        return false;
    }

    public boolean pm(User user, String pm) {
        if (!DATABASE.isRegistered(user.getUsername())) return false;
        if (this.isOnline()){
            if (this.followingList.indexOf(user) == -1) return false;
            else {
                if (this.pms.keySet().contains(user)) this.pms.get(user).add(pm);
                else {
                    this.pms.put(user, new Vector<String>());
                    this.pms.get(user).add(pm);
                }
                return true;
            }
        }
        return false;
    }

    public String getStats() {
        return this.getAge() + " " + this.posts.size() + " " + this.followersList.size() + " " + this.followingList.size();
    }

}
