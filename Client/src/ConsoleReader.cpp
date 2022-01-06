#include "../include/ConsoleReader.h"
#include <mutex>
#include <iostream>
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"
#include <thread>

using namespace std;

ConsoleReader::ConsoleReader(ConnectionHandler& cHandler, bool* shouldTerminate,bool* ready):
    cHandler(cHandler), 
    shouldTerminate(shouldTerminate),
    ready(ready)
    {};

void ConsoleReader::run() 
{
    while (!(*shouldTerminate)) {   
        std::string message;
        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<std::string> msg;
        boost::split(msg, line, boost::is_any_of(" "));
        char opCode[2];
        boost::to_upper(msg[0]); // Corrects lowercase input
        if (msg[0] == "REGISTER")
        {   
            if (msg.size() != 4 || msg[3].size() == 0) 
            {
                cout<<"ERROR Bad input"<<endl;
                continue; // Break current iteration if some information is missing or too much information exists
            }
            std::vector<std::string> date;
            boost::split(date, msg[3], boost::is_any_of("-"));
            if (date[0].size() > 2 || date[0].size() < 1 || date[1].size() > 2 || date[1].size() < 1 || date[2].size() > 4 || date[2].size() < 1)
            {
                cout<<"ERROR Bad input"<<endl;
                continue; // Break current iteration if date is borked
            }
            bool stop = false;
            for (std::string part : date) 
            {   
                for (char& ch : part)
                {
                    if (isdigit(ch) == 0)
                        stop = true;
                    if (stop) break;
                }
            }
            if (stop) {
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
            shortToBytes(1, opCode);
            message = msg[1] + '\0' + msg[2] + '\0' + msg[3] + '\0';
        }
        else if (msg[0] == "LOGIN")
        {
            if (msg.size() != 4)
            {
                cout<<"ERROR Bad input"<<endl; 
                continue; // Missing info or has too much info
            }
            shortToBytes(2, opCode);
            message = msg[1] + '\0' + msg[2] + '\0' + msg[3] + '\0';
        }
        else if (msg[0] == "LOGOUT")
        {
            if (msg.size() != 1)
            {
                cout<<"ERROR Bad input"<<endl;
                continue;
            } 
            *ready = false;
            shortToBytes(3, opCode);
            message = '\0';
            this -> cHandler.sendBytes(opCode, 2);
            this -> cHandler.sendLine(message);
            while(!*ready)
             std::this_thread::yield();
            *ready = false; 
            continue;
        }
        else if (msg[0] == "FOLLOW")
        {
            if (msg.size() != 3 || msg[2].size() == 0) 
            {
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
            shortToBytes(4, opCode);
            message = msg[1] + '\0' + msg[2] + '\0';
            
        }
        else if (msg[0] == "POST")
        {
            if (msg.size() <= 1) 
            {
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
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
            if (msg.size() <=2) 
            {
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
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
            if (msg.size() > 1) 
            {
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
            shortToBytes(7, opCode);
            message = '\0';
        }
        else if (msg[0] == "STAT")
        {
            if (msg.size() <=1)
            { 
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
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
            if (msg.size() <= 1) 
            {
                cout<<"ERROR Bad input"<<endl;
                continue;
            }
            shortToBytes(12, opCode);
            message = msg[1] + '\0';
        }
        else 
        {
            cout<<"ERROR Bad input"<<endl;
            continue;
        }
        if (!*shouldTerminate) 
        {
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