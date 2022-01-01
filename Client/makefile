CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: Client
	g++ -o bin/BGSclient bin/connectionHandler.o bin/Client.o bin/SocketReader.o bin/ConsoleReader.o $(LDFLAGS)

Client: bin/connectionHandler.o bin/Client.o bin/SocketReader.o bin/ConsoleReader.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/SocketReader.o: src/SocketReader.cpp
	g++ $(CFLAGS) -o bin/SocketReader.o src/SocketReader.cpp

bin/ConsoleReader.o: src/ConsoleReader.cpp
	g++ $(CFLAGS) -o bin/ConsoleReader.o src/ConsoleReader.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp
	
.PHONY: clean
clean:
	rm -f bin/*