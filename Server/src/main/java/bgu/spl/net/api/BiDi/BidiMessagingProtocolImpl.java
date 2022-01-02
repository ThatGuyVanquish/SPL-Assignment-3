package bgu.spl.net.api.BiDi;

import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.Database;
import bgu.spl.net.User;
import bgu.spl.net.api.EncDecImpl;
import bgu.spl.net.srv.ConnectionHandler;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<String>{

    private Connections connections;
    private int connectionId;
    private static final Database DATABASE = Database.getInstance();
    private User user;

    @Override
    public void start(int connectionId, Connections connections) {
        this.connections = connections;
        this.connectionId = connectionId;
        this.user = null;
    }

    @Override
    public void process(String message) {
        String[] msg = message.split("\0");
        int opCode = Integer.parseInt(msg[0]);
        if (opCode == 1) { // Register
            String username = msg[1]; // need to check this actually works as intended
            if (!DATABASE.isRegistered(username)) {
                User newUser = new User(username, msg[1], msg[2]);
                DATABASE.register(username, newUser);
            }
            else this.connections.send(this.connectionId, "11 1"); // Send an error to the client
        }
        else if (opCode == 2) { // Login
            String username = msg[1];
            if (this.user != null) { // This client already has a logged in user
                this.connections.send(this.connectionId, "11 2");
            }
            // Need to add captcha check
            if (!DATABASE.isRegistered(username) || !DATABASE.getUser(username).login(username, msg[2])) { // Login failed because of an incorrect username or password
                this.connections.send(this.connectionId, "11 2");
            }
            else {
                this.connections.send(this.connectionId, " 10 2");
            }
            
        }
        else if (opCode == 3) { // Logout
            if (this.user == null) { // This client already isn't logged in yet
                this.connections.send(this.connectionId, "11 3");
            }
            else {
                this.user.logout();
                this.connections.send(this.connectionId, "10 3");
                DATABASE.removeClient(this.connectionId);
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

        }
        else if (opCode == 6) { // PM

        }
        else if (opCode == 7) { // Logstat

        }
        else if (opCode == 8) { // Stat

        }
        else if (opCode == 12) { // Block

        }
    }

    @Override
    public boolean shouldTerminate() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
