#ifndef SOCKET_READER__
#define SOCKET_READER__

#include "connectionHandler.h"      

using namespace std;

class SocketReader{
    private: 
    ConnectionHandler& cHandler;
    bool* shouldTerminate;
    bool* ready;
    public:
    SocketReader(ConnectionHandler& cHandler, bool* shouldTerminate, bool* ready);
    void run();
    short bytesToShort(char* bytesArr);
};


#endif