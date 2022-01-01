package bgu.spl.net.api.BiDi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.net.srv.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConnectionsImpl() {
        this.clientMap = new ConcurrentHashMap<>();
        this.currentId = new AtomicInteger(0);
    }

    private static class singletonHolder {
        private static final ConnectionsImpl instance = new ConnectionsImpl();
    }

    public static ConnectionsImpl getInstance() {
        return singletonHolder.instance;
    }

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> clientMap;
    private AtomicInteger currentId;

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
    
    public void connect(ConnectionHandler cHandler) {
        this.clientMap.put(this.currentId.getAndIncrement(), cHandler);
    }

}
