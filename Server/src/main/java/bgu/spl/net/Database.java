package bgu.spl.net;

import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.srv.ConnectionHandler;

public class Database {
    private ConcurrentHashMap<Integer, ConnectionHandler> cHandlerMap;
    private ConcurrentHashMap<ConnectionHandler, User> userMap; // Maybe irrelevant as the BidiMessagingProtocol has a user field
    private ConcurrentHashMap<String, User> registeredUsers;

    private static class singletonHolder {
        private static final Database instance = new Database();
    }

    public static Database getInstance() {
        return singletonHolder.instance;
    }

    private Database() {
        this.cHandlerMap = new ConcurrentHashMap<>();
        this.userMap = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    public void register(String username, User user) {
        this.registeredUsers.put(username, user);
    }

    public boolean isRegistered(String username) {
        return (registeredUsers.get(username) != null);
    }

    public User getUser(String username) {
        return this.registeredUsers.get(username);
    }

    public void removeClient(int connectionId) {
        ConnectionHandler toRemove = this.cHandlerMap.get(connectionId);
        this.userMap.remove(toRemove);
        this.cHandlerMap.remove(connectionId);
    }
}
