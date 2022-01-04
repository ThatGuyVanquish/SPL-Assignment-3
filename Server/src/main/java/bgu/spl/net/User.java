package bgu.spl.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

public class User {
    
    private Database DATABASE = Database.getInstance();
    private int connecionId;
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
    private Vector<String> incoming;
    
    public User( String username, String password, String birthday, int connectionId) {
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.connecionId = connectionId;
        this.followingList = new Vector<>();
        this.followersList = new Vector<>();
        this.blockList = new Vector<>();
        this.blockedBy = new Vector<>();
        this.loginStatus = false;
        this.posts = new Vector<>();
        this.pms = new HashMap<>();
        this.incoming = new Vector<>();
    }   

    public boolean login(String username, String password, String captcha) {
        if (this.isOnline()) return false;
        if (!captcha.equals("1")) return false;
        if (username.equals(this.username) && password.equals(this.password)) {
            this.loginStatus = true;
            return true;
        }
        return false;
    }

    public void setConId(int i) {
        this.connecionId = i;
    }

    public int getConId() {
        return this.connecionId;
    }

    public boolean logout() {
        if (this.isOnline()) {
            this.loginStatus = false;
            this.connecionId = -1;
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
        if (this.followingList.indexOf(username) == -1 && this.blockList.indexOf(username) == -1 && this.blockedBy.indexOf(username) == -1){
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
        if (!DATABASE.isRegistered(username.getUsername())) {
            return false;
        }
        if (this.followingList.indexOf(username) != -1) {
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

    public boolean isFollowing(User username) {
        for (User user : this.followingList) {
            if (user == username) return true;
        }
        return false;
    }

    public Vector<User> getFollowers() {
        return this.followersList;
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
        if (!DATABASE.isRegistered(username.getUsername()) || this.blockList.contains(username)) return false;
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
        String[] bday = this.birthday.split("-");
        int day = Integer.parseInt(bday[0]);
        int month = Integer.parseInt(bday[1]);
        int year = Integer.parseInt(bday[2]);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        double age = currentYear - year - (currentMonth - month)/12 - (currentDay - day)/365;
        return age;
    }

    public boolean post(String post) {
        if (this.isOnline()) {
            this.posts.add(post);
            return true;
        }
        return false;
    }

    public String pm(User user, String pm) {
        pm = DATABASE.filterString(pm);
        if (this.pms.keySet().contains(user)) this.pms.get(user).add(pm);
        else {  
            this.pms.put(user, new Vector<String>());
            this.pms.get(user).add(pm);
        }
        return pm;
    }

    public String getStats() {
        return this.getAge() + " " + this.posts.size() + " " + this.followersList.size() + " " + this.followingList.size();
    }

    public void incomingMsg(String str) {
        this.incoming.add(str);
    }

    public String getIncomingMsg() {
        if (!this.incoming.isEmpty())
            return this.incoming.remove(0);
        return null;
    }

    public boolean isBlocked(User user) {
        return this.blockedBy.indexOf(user) != -1;
    }

}
