#ifndef SOCKET_READER__
#define SOCKET_READER__

#include "connectionHandler.h"                    

class SocketReader{
    private: 
    ConnectionHandler& cHandler;
    bool* shouldTerminate;

    public:
    SocketReader(ConnectionHandler& cHandler, bool* shouldTerminate);
    void run();
    short bytesToShort(char* bytesArr);
};


#endif