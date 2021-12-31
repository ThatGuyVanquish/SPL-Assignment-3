#ifndef SOCKET_READER__
#define SOCKET_READER__

#include <mutex>;
#include "connectionHandler.h";                       

class socketReader{
    private: 
    int id;
    std::mutex& mutex;
    ConnectionHandler& cHandler;
    bool* shouldTerminate;

    public:
    socketReader(int id, std::mutex& mutex, ConnectionHandler& cHandler, bool* shouldTerminate);
    void run();
    void bytesToShort(char* bytesArr);
};


#endif