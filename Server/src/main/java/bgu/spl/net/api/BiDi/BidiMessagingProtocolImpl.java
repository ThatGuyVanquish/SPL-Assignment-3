package bgu.spl.net.api.BiDi;

import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.User;
import bgu.spl.net.api.EncDecImpl;
import bgu.spl.net.srv.ConnectionHandler;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<String>{

    private ConcurrentHashMap<Integer, User> userMap;
    private ConcurrentHashMap<String, User> usernameMap;

    private Connections connectionsImpl;
    private int connectionId;
    private User user;

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionsImpl = connections;
        this.connectionId = connectionId;
    }

    @Override
    public void process(String message) {
        String[] msg = message.split("\0");
        int opCode = Integer.parseInt(msg[0].substring(0,2));
        msg[0] = msg[0].substring(2);
        if (opCode == 1) { // Register
            String username = msg[0];
            if (usernameMap.get(username) == null) {
                User newUser = new User(username, msg[1], msg[2]);
                // add user to database
            }
        }
        else if (opCode == 2) { // Login

        }
        else if (opCode == 3) { // Logout

        }
        else if (opCode == 4) { // Follow/Unfollow

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
