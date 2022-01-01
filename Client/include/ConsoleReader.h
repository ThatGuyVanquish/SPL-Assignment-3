#ifndef CONSOLE_READER__
#define CONSOLE_READER__

#include "connectionHandler.h"

class ConsoleReader{
    private: 
    ConnectionHandler& cHandler;
    bool* shouldTerminate;

    public:
    ConsoleReader(ConnectionHandler& cHandler, bool* shouldTerminate);
    void run();
    void shortToBytes(short num, char* bytesArr);
};

#endif