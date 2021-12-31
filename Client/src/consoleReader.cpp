#include "../include/consoleReader.h"
#include <mutex>
#include <iostream>
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"

using namespace std;

consoleReader::consoleReader(int id, std::mutex& mutex, ConnectionHandler& cHandler, bool* shouldTerminate):
    id(id), 
    mutex(mutex), 
    cHandler(cHandler), 
    shouldTerminate(shouldTerminate)
    {};

void consoleReader::run() 
{
    while (!(*shouldTerminate))
    {
        std::string message;
        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<std::string> msg;
        boost::split(msg, line, boost::is_any_of(" "));
        char opCode[2];
        if (msg[0] == "REGISTER")
        {
            message = "1" + '\0' + msg[1] + '\0' + msg[2] + '\0' + msg[3] + ';';
        }
        else if (msg[0] == "LOGIN")
        {
            message = "2" + '\0' + msg[1] + '\0' + msg[2] + '\0' + "1" + ';';
        }
        else if (msg[0] == "LOGOUT")
        {
            message = "3" + ';';
        }
        else if (msg[0] == "FOLLOW")
        {
            message = "4" + '\0' + msg[1] + '\0' + msg[2] + ';';
        }
        else if (msg[0] == "POST")
        {
            message = "5" + '\0';
            for (int i = 1; i < msg.size() - 1; i++) 
            {
                message.append(msg[i] + '\0');
            }
            message.append(msg[msg.size()-1] + ';');
        }
        else if (msg[0] == "PM")
        {
            message = "6" + '\0' + msg[1] + '\0';
            for (int i = 1; i < msg.size(); i++) 
            {
                message.append(msg[i] + '\0');
            }
            time_t rawtime;
            struct tm * timeinfo;
            char buffer[80];
            time (&rawtime);
            timeinfo = localtime(&rawtime);
            strftime(buffer,sizeof(buffer),"%d-%m-%Y %H:%M",timeinfo);
            std::string str(buffer);
            message.append(str + ';');
        }
        else if (msg[0] == "LOGSTAT")
        {
            message = "7;";
        }
        else if (msg[0] == "STAT")
        {
            message = "8" + '\0';
            for (int i = 1; i < msg.size() - 1; i++) 
            {
                message.append(msg[i] + '|');
            }
            message.append(msg[msg.size() - 1] + ';');
        }
        else if (msg[0] == "BLOCK")
        {
            message = "12" + '\0' + msg[1] + ';';
        }
        cHandler.sendLine(message);
    }
}

void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}