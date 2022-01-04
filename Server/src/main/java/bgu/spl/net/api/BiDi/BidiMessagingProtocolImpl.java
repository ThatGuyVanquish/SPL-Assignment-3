package bgu.spl.net.api.BiDi;

import java.util.Vector;
import bgu.spl.net.Database;
import bgu.spl.net.User;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<String>{

    private Connections<String> connections;
    private int connectionId;
    private static final Database DATABASE = Database.getInstance();
    private User user;
    private boolean shouldTerminate;

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connections = connections;
        this.connectionId = connectionId;
        this.user = null;
        this.shouldTerminate = false;
    }

    @Override
    public void process(String message) {
        String[] msg = message.split("\0");
        int opCode = Integer.parseInt(msg[0]);
        if (opCode == 1) { // Register
            String username = msg[1]; // need to check this actually works as intended
            if (!DATABASE.isRegistered(username)) {
                User newUser = new User(username, msg[2], msg[3], -1);
                DATABASE.register(username, newUser);
                this.connections.send(this.connectionId, "10 1");
            }
            else this.connections.send(this.connectionId, "11 1"); // Send an error to the client
        }
        else if (opCode == 2) { // Login
            String username = msg[1];
            if (this.user != null) { // This client already has a logged in user
                this.connections.send(this.connectionId, "11 2");
            }
            else if (!DATABASE.isRegistered(username) || !DATABASE.getUser(username).login(username, msg[2], msg[3])) { // Login failed because of an incorrect username or password        
                this.connections.send(this.connectionId, "11 2");
            }
            else {
                this.connections.send(this.connectionId, "10 2");
                this.user = DATABASE.getUser(username);
                this.user.setConId(this.connectionId);
                while (true) {
                    String msgToPrint = this.user.getIncomingMsg();
                    if (msgToPrint != null)
                        this.connections.send(this.connectionId, msgToPrint);
                    else break;
                }
            }
        }
        else if (opCode == 3) { // Logout
            if (this.user == null) { // This client already isn't logged in yet
                this.connections.send(this.connectionId, "11 3");
            }
            else {
                this.user.logout();
                this.connections.send(this.connectionId, "10 3");
                this.shouldTerminate = true;
            }
        }
        else if (opCode == 4) { // Follow/Unfollow
            int followOpCode = Integer.parseInt(msg[1]);
            if (this.user == null) 
                this.connections.send(this.connectionId, "11 4");
            else if (followOpCode == 1) { // Try to follow @msg[2]
                if (this.user.follow(DATABASE.getUser((msg[2])))) {
                    this.connections.send(this.connectionId, "10 4 " + msg[2]);
                }
                else {
                    this.connections.send(this.connectionId, "11 4"); // Couldn't follow
                }
            }
            else if (followOpCode == 0) { // Try to unfollow @msg[2]
                if (this.user.unfollow(DATABASE.getUser(msg[2]))) {
                    this.connections.send(this.connectionId, "10 4 " + msg[2]);
                }
                else {
                    this.connections.send(this.connectionId, "11 4"); // Couldn't unfollow
                }
            }
        }
        else if (opCode == 5) { // Post
            if (this.user == null) 
                this.connections.send(this.connectionId, "11 5");
            else {
                String post = "";
                //this.user.post(msg[1]);
                Vector<User> sendTo = new Vector<>();
                for (int i = 1; i < msg.length; i++) {
                    if (msg[i].indexOf('@') == 0) {
                        User atMention = DATABASE.getUser(msg[i].substring(1));
                        if (atMention != null && !atMention.isFollowing(this.user))
                            sendTo.add(atMention);
                    }
                    post = post + msg[i] + " ";
                }
                post = post.substring(0, post.length()-1);
                this.user.post(post);
                sendTo.addAll(this.user.getFollowers());
                for (User user : sendTo) {
                    if (user.isOnline())
                        this.connections.send(user.getConId(), "9 1 " + this.user.getUsername() + " " + post + ";");
                    else
                        user.incomingMsg("9 1 " + this.user.getUsername() + " " + post + ";");
                }
                this.connections.send(this.connectionId, "10 5");
            }
        }
        else if (opCode == 6) { // PM
            if (this.user == null)
                this.connections.send(this.connectionId, "11 6");
            else {
                String username = msg[1];
                User sendTo = DATABASE.getUser(username);
                if (sendTo == null || !this.user.isFollowing(sendTo))
                    this.connections.send(this.connectionId, "11 6");
                else {
                    int endOfUsername = msg[2].indexOf(" ");
                    String pmMSG = this.user.pm(sendTo, msg[2].substring(endOfUsername + 1));
                    if (sendTo.isOnline())
                        this.connections.send(sendTo.getConId(), "9 0 " + this.user.getUsername() + " " + pmMSG + ";");
                    else
                        sendTo.incomingMsg("9 0 " + this.user.getUsername() + " " + pmMSG + ";");
                    this.connections.send(this.connectionId, "10 6");
                }
            }
        }
        else if (opCode == 7) { // Logstat
            if(this.user == null)
                this.connections.send(this.connectionId, "11 7");
            else{
                for (User user : DATABASE.getUsers()) {
                    if (user.isOnline()) {
                        if (this.user.isBlocked(user))
                            this.connections.send(this.connectionId, "11 7");
                        else
                            this.connections.send(this.connectionId, "10 7 " + user.getStats());
                    }
                }
            } 
        }
        else if (opCode == 8) { // Stat
            if(this.user == null)
                this.connections.send(this.connectionId, "11 8");
            else{
                String[] users = msg[1].split("\\|");
                for (String user : users) {
                    User currentUser = DATABASE.getUser(user);
                    if (this.user.isBlocked(currentUser))
                        this.connections.send(this.connectionId, "11 8");
                    else
                        this.connections.send(this.connectionId, "10 8 " + currentUser.getStats());
                }
            } 
        }
        else if (opCode == 12) { // Block
            if(this.user == null)
                this.connections.send(this.connectionId, "11 12");
            else{
                if (this.user.block(DATABASE.getUser(msg[1]))) {
                    this.connections.send(this.connectionId, "10 12 " + msg[1]);
                }
                else {
                    this.connections.send(this.connectionId, "11 12");
                }
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        if (this.shouldTerminate != false)
            DATABASE.removeClient(this.connectionId);
        return this.shouldTerminate;
    }
    
}
