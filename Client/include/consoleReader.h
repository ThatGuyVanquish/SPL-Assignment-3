#ifndef CONSOLE_READER__
#define CONSOLE_READER__

#include <mutex>
#include "connectionHandler.h"

class consoleReader{
    private: 
    int id;
    std::mutex& mutex;
    ConnectionHandler& cHandler;
    bool* shouldTerminate;

    public:
    consoleReader(int id, std::mutex& mutex, ConnectionHandler& cHandler, bool* shouldTerminate);
    void run();
    void shortToBytes(short num, char* bytesArr);
};

#endif