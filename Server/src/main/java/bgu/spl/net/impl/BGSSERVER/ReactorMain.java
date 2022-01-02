package bgu.spl.net.impl.BGSSERVER;

import bgu.spl.net.api.EncDecImpl;
import bgu.spl.net.api.BiDi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        Server<String> server = Server.reactor(Integer.parseInt(args[1]), 
        Integer.parseInt(args[0]),
        () -> new BidiMessagingProtocolImpl(),
        () -> new EncDecImpl());
        server.serve();
    }
}