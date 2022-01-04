#include <stdlib.h>
#include "../include/connectionHandler.h"
#include "../include/ConsoleReader.h"
#include "../include/SocketReader.h"
#include <thread>

using namespace std;

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
	
    bool* shouldTerminate = new bool(false);
    bool* ready = new bool(false);
    ConsoleReader crTask(connectionHandler, shouldTerminate,ready);
    SocketReader srTask(connectionHandler, shouldTerminate,ready);
    thread srThread(&SocketReader::run, &srTask);
    thread crThread(&ConsoleReader::run, &crTask);
    

    crThread.join();
    srThread.join();

    delete shouldTerminate;   
    delete ready; 

    return 0;
}
