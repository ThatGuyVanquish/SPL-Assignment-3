package bgu.spl.net;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.srv.ConnectionHandler;

public class Database {
    private ConcurrentHashMap<Integer, ConnectionHandler> cHandlerMap;
    private ConcurrentHashMap<ConnectionHandler, User> userMap; // Maybe irrelevant as the BidiMessagingProtocol has a user field
    private ConcurrentHashMap<String, User> registeredUsers;
    private static final Vector<String> restrictedWords = new Vector<>();

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
        restrictedWords.add("blyn"); restrictedWords.add("69420");
        restrictedWords.add("a little goat"); restrictedWords.add("a man with an arrow");
        restrictedWords.add("winnie the pooh"); restrictedWords.add("friends");
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

    public String filterString(String msg) {
        for (String str : restrictedWords) {
            msg.replaceAll(str, "<filtered>");
        }
        return msg;
    }

    public void removeClient(int connectionId) {
        ConnectionHandler toRemove = this.cHandlerMap.get(connectionId);
        this.userMap.remove(toRemove);
        this.cHandlerMap.remove(connectionId);
    }
}
