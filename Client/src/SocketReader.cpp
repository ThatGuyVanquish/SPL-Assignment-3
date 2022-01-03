#include "../include/SocketReader.h"
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"
using namespace std;


SocketReader::SocketReader(ConnectionHandler &handler, bool* shouldTerminate): 
cHandler(handler), 
shouldTerminate(shouldTerminate)
{};

void SocketReader::run() {
    while (!(*shouldTerminate)) {
        //mutex.try_lock();
        char message[2];
        cHandler.getBytes(message, 2); // Receives Server to Client message op-code
        short opCode = bytesToShort(message);
        if (opCode == 11) {
            cHandler.getBytes(message, 2); // Receives Client to Server message op-code (the one for which this message was received)
            short messageOpCode = bytesToShort(message);
            cout << "ERROR " << messageOpCode << endl;
        }
        else if (opCode == 10){
            cHandler.getBytes(message, 2); // Receives Client to Server message op-code (the one for which this message was received)
            short messageOpCode = bytesToShort(message);
            cout << "ACK " << messageOpCode;
            std::string optional;
            if (cHandler.getLine(optional))
                cout << optional << endl;
            if (messageOpCode == 3) {
                cout<<"logout called"<<endl;
                *shouldTerminate = true;
            }
        }
        else if (opCode == 9)
        {
            cHandler.getBytes(message, 2);
            short notificationType = bytesToShort(message);
            std::string notiType;
            if (notificationType == 0) 
            {
                notiType = "PM";
            }
            else 
            {
                notiType = "Public";
            }
            std::string message;
            cHandler.getLine(message);
            cout<< "NOTIFICATION " << notiType + " " << "@" + message<<endl;
        }
    }
}

short SocketReader::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}