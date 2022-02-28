#ifndef CONSOLE_READER__
#define CONSOLE_READER__

#include "connectionHandler.h"

#include <iostream>
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"
#include <thread>

using namespace std;

class ConsoleReader{
    private: 
    ConnectionHandler& cHandler;
    bool* shouldTerminate;
    bool* ready;
    
    public:
    ConsoleReader(ConnectionHandler& cHandler, bool* shouldTerminate, bool* ready);
    void run();
    void shortToBytes(short num, char* bytesArr);
};

#endif