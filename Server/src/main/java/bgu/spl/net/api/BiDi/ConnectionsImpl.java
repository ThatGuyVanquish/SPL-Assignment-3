package bgu.spl.net.api.BiDi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.net.srv.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> clientMap;

    private ConnectionsImpl() {
        this.clientMap = new ConcurrentHashMap<>();
    }

    private static class singletonHolder {
        private static final ConnectionsImpl instance = new ConnectionsImpl();
    }

    public static ConnectionsImpl getInstance() {
        return singletonHolder.instance;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> currentClient = this.clientMap.get(connectionId);
        currentClient.send(msg);
        return false; // Why the fuck is this boolean
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> ch : this.clientMap.values()) {
            ch.send(msg);
        }    
    }

    @Override
    public void disconnect(int connectionId) {
        this.clientMap.remove(connectionId);
    }
    
    public void connect(int id,ConnectionHandler cHandler) {
        this.clientMap.put(id, cHandler);
    }

}
