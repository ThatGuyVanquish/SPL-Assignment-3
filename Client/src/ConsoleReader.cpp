#include "../include/ConsoleReader.h"
#include <mutex>
#include <iostream>
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"

using namespace std;

ConsoleReader::ConsoleReader(ConnectionHandler& cHandler, bool* shouldTerminate):
    cHandler(cHandler), 
    shouldTerminate(shouldTerminate)
    {};

void ConsoleReader::run() 
{
    while (!(*shouldTerminate)) {   
        std::string message;
        const short bufsize = 1024;
        char buf[bufsize];
        if (!*shouldTerminate) cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<std::string> msg;
        boost::split(msg, line, boost::is_any_of(" "));
        char opCode[2];
        if (msg[0] == "REGISTER")
        {
            shortToBytes(1, opCode);
            message = msg[1] + '\0' + msg[2] + '\0' + msg[3] + '\0';
        }
        else if (msg[0] == "LOGIN")
        {
            shortToBytes(2, opCode);
            message = msg[1] + '\0' + msg[2] + '\0' + msg[3] + '\0';
        }
        else if (msg[0] == "LOGOUT")
        {
            shortToBytes(3, opCode);
            message = '\0';
        }
        else if (msg[0] == "FOLLOW")
        {
            shortToBytes(4, opCode);
            message = msg[1] + '\0' + msg[2] + '\0';
        }
        else if (msg[0] == "POST")
        {
            shortToBytes(5, opCode);
            message = "";
            for (int i = 1; i < static_cast<int>(msg.size()) - 1; i++) 
            {
                message.append(msg[i] + '\0');
            }
            message.append(msg[msg.size()-1] + '\0');
        }
        else if (msg[0] == "PM")
        {
            shortToBytes(6, opCode);
            message = msg[1] + '\0';
            for (int i = 1; i < static_cast<int>(msg.size()); i++) 
            {
                message.append(msg[i] + ' ');
            }
            time_t rawtime;
            struct tm * timeinfo;
            char buffer[80];
            time (&rawtime);
            timeinfo = localtime(&rawtime);
            strftime(buffer,sizeof(buffer),"%d-%m-%Y %H:%M",timeinfo);
            std::string str(buffer);
            message.append(str + '\0');
        }
        else if (msg[0] == "LOGSTAT")
        {
            shortToBytes(7, opCode);
            message = '\0';
        }
        else if (msg[0] == "STAT")
        {
            shortToBytes(8, opCode);
            message = "";
            for (int i = 1; i < static_cast<int>(msg.size()) - 1; i++) 
            {
                message.append(msg[i] + '|');
            }
            message.append(msg[msg.size() - 1] + '\0');
        }
        else if (msg[0] == "BLOCK")
        {
            shortToBytes(12, opCode);
            message = msg[1] + '\0';
        }
        if (!*shouldTerminate) {
            this -> cHandler.sendBytes(opCode, 2);
            this -> cHandler.sendLine(message);
        }
    }
}

void ConsoleReader::shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}