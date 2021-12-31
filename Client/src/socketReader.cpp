#include "../include/socketReader.h"
#include <mutex>
using namespace std;


socketReader::socketReader(int id, std::mutex &mutex, ConnectionHandler &handler, bool* shouldTerminate): 
id(id), 
mutex(mutex), 
cHandler(handler), 
shouldTerminate(shouldTerminate)
{};

void socketReader::run() {
    while (!(*shouldTerminate)) {
        mutex.try_lock();
        char message[2];
        cHandler.getBytes(message, 2);
        short opCode = bytesToShort(message);
        if (opCode == 11) {
            cHandler.getBytes(message, 2);
            short messageOpCode = bytesToShort(message);
            cout << "ERROR " << messageOpCode << std::endl;
            if (messageOpCode == 3) {
                mutex.unlock();
                sleep(1);
            }
        }
        else if (opCode == 10){
            cHandler.getBytes(message, 2);
            short messageOpCode = bytesToShort(message);
            cout << "ACK " << messageOpCode << std::endl;
            string optional;
            cHandler.getLine(optional);
            if (optional != "")
                cout << optional << std::endl;
            if (messageOpCode == 4) {
                *shouldTerminate = true;
                mutex.unlock();
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
                notiType = "POST";
            }

            //cout << "NOTIFICATION " + notiType + " "
        }
    }
}

short socketReader::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}