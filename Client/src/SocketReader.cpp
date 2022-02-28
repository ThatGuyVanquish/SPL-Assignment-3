#include "../include/SocketReader.h"
#include <iostream>
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"
#include <thread>

using namespace std;


SocketReader::SocketReader(ConnectionHandler& handler, bool* shouldTerminate, bool* ready): 
    cHandler(handler), 
    shouldTerminate(shouldTerminate),
    ready(ready)
{};

void SocketReader::run() {
    while (!(*shouldTerminate)) {
        char message[2];
        
        cHandler.getBytes(message, 2); // Receives Server to Client message op-code
        short opCode = bytesToShort(message);
        if (opCode == 11) {
            cHandler.getBytes(message, 2); // Receives Client to Server message op-code (the one for which this message was received)
            short messageOpCode = bytesToShort(message);
            cout << "ERROR " << messageOpCode << endl;
            std::string useless;
            cHandler.getLine(useless);
            *ready = true;
        }
        else if (opCode == 10){
            cHandler.getBytes(message, 2); // Receives Client to Server message op-code (the one for which this message was received)
            short messageOpCode = bytesToShort(message);
            cout << "ACK " << messageOpCode;
            std::string optional;
            if (cHandler.getLine(optional))
                cout << " " + optional.substr(0, optional.length() - 1) << endl;
            if (messageOpCode == 3) {
                *shouldTerminate = true;
                *ready = true;
                continue;
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
            message = message.substr(0,message.length()-1);
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