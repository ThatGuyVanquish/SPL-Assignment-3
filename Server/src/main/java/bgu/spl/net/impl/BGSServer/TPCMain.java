package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.EncDecImpl;
import bgu.spl.net.api.BiDi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        //Server<String> server = Server.threadPerClient(Integer.parseInt(args[0]), 
        Server<String> server = Server.threadPerClient(Integer.parseInt("7777"), //used for testing
        () -> new BidiMessagingProtocolImpl(),
        () -> new EncDecImpl());
        server.serve();
    }
}
