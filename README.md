BGU Course Systems Programming, Assignment 3 Created by Tom and Nave Grade: 100

Written in Java, C++

The Assignment:
Simulate a social network, using Thread Per Client and Reactor design patterns.
The servers were programmed in Java and the console based client was programmed in C++.Cancel changes
This assignment tested our skills in C++, Java, threading and concurrency, server design patterns.

1) How to run the code:
1.1)
Reactor Server:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 10"
TPC Server:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"
1.2)
(1)  REGISTER username password 01-02-0003
(2)  LOGIN user pw 1
(3)  LOGOUT
(4)  FOLLOW 1 user
(5)  POST content @user content content content
(6)  PM user content
(7)  LOGSTAT
(8)  STAT user1 user2 user3
(12) BLOCK user

2) Filtered word list is located in the Database class (bgu/spl/net/Database.java)
   as the String Vector restrictedWords, filtered words are hardcoded within the 
   Database class constructor.
