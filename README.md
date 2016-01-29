# GoGame
GoGame: Game Logic, Computer Players, Server/Client, Protocol and extra's

DOWNLOAD
- Download the the latest commit from the branch Michiel

COMPILATION
- Server/Client: javac go_game/server/*.java 
- Protocols:  javac go_game/protocol/*.java
- Game files: javac go_game/*.java

STARTING SERVER
- usage: go_game.server.Server <port>


STARTING CLIENT:
There are two clients: 
1. One simple 'Client' made to communicate with the Arduino board in the future.
2. One paring 'ParsingClient' to communicate with the rest of the Nedap University team.

Parsing client:
- usage: java go_game.server.ClientParsing <name> <address> <port>
Simple client:
- usage: go_game.server.Client <name> <address> <port>

FUNCTIONS
See 'GETFUNCTIONS', no matter what state your in!
