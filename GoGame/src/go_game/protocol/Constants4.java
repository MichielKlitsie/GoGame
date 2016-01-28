package go_game.protocol;

/**
 * Go! Protocol
 *
 * The client makes a connnection to the server via a Socket. The client and the server communicate via messages that
 * are Strings. Messages are of the following format:
 *
 * COMMAND ARG1 ARG2 ARG3
 *
 * where the arguments are separated by spaces and the number of arguments is command specific. This is a line based
 * protocol, such that every combination of command and arguments is on a new line.
 *
 * The server port should be configurable. By default the server will use port 1929 to wait for connecting clients. The
 * Server has to determine the board size, such that clients that connect to the server will automatically play the
 * boardsize determined by the server. Possible boardsizes are 9, 13 and 19 and it is assumed that the client supports
 * these.
 *
 * The server has the responsibility to keep track of the game state, that includes what stones are one the board,
 * how many captives of each color there are, whose turn it is, how many subsequent passes have occurred. The server
 * also has the responsibility to determine whether or not a move is valid. If a move is not valid, it will allow the
 * client to choose another move.
 *
 * If the client sends a command to the server that is not part of the protocol, the server may close the connection and
 * end the game. If the client sends a command that is part of the protocol, but not supported by the server, the server
 * may not close the connection. If the server sends a non- supported command to the client, the client should continue
 * as normal. If connection to a client is lost or a client is not responsive for more than 60 seconds, the server ends
 * the game and informs the players of this.
 *
 * At this moment, we end the game if two players consecutively pass. How to deal with an appropriate game ending has to
 * be further elaborated.
 *
 * @author michiel.klitsie
 * @author joris.vandijk
 * @author ron.weikamp
 * @author liesbeth.wijers
 * @version 1.0
 */

public interface Constants4 {
    // General
    public static final String VERSION = "VERSION 1.0";
    public static final int SERVER_PORT = 1929;
    public static final int TIMEOUTSECONDS = 60;

    // Delimiters
    /**
     * Delimiter between command and arguments.
     */
    public static final String DELIMITER = " ";
    /**
     * Delimiter for 'subarguments', necessary for the Observer protocol
     */
    public static final String SUB_DELIMITER = ";";

    // Basic game
    // Commands
    /**
     * Examples of how the following constants are used:
     * - Client -> Server:  NEWPLAYER Jan
     * - Client <-> Server: GETOPTIONS
     * - Client <-> Server: OPTIONS PLAY
     * - Client -> Server:  PLAY
     * - Server -> Client:  WAITFOROPPONENT
     * - Server -> Server:  GAMESTART Jan 9 BLACK           (name, boardsize, clientcolor)
     * - Client -> Server:  MOVE 3 4                        (row, column:  0 <= r/c && r/c < boardsize)
     * - Server -> Client:  MOVE BLACK 3 4
     * - Client -> Server:  MOVE PASS
     * - Server -> Client:  MOVE BLACK PASS
     * - Client -> Server:  GETBOARD
     * - Server -> Client:  BOARD EEEEEEBEEWWEE... 3 16     (board repr., black captives , white captives)
     * - Client -> Server:  QUIT
     * - Server -> Client:  GAMEOVER
     *
     * A client should always say his name first using NEWPLAYER. If a Client sends NEWPLAYER, the SERVER
     * should always respond with NEWPLAYERACCEPTED.
     *
     * A client can always send GETOPTIONS to the server, the server should then respond with OPTIONS plus
     * all the commands that the client may send.
     *
     * A client can always send GETEXTENSIONS to the server, the server should respond with EXTENSIONS and
     * the designated keywords of the things it responds. This also works vice versa. The server can always
     * send GETEXTENSIONS, the client should respond with EXTENSIONS.
     *
     * The boardstring format is: starting in the upper left corner, traverse all the intersection of the board row by
     * row. Each white, black and empty intersection is represented by a designated constant.
     */
    public static final String NEWPLAYER  = "NEWPLAYER";
    public static final String NEWPLAYERACCEPTED = "NEWPLAYERACCEPTED";
    public static final String GETOPTIONS = "GETOPTIONS";
    public static final String OPTIONS = "OPTIONS";
    public static final String GETEXTENSIONS = "GETEXTENSIONS";
    public static final String EXTENSIONS = "EXTENSIONS";
    public static final String PLAY = "PLAY";
    public static final String WAITFOROPPONENT = "WAITFOROPPONENT";
    public static final String GAMESTART = "GAMESTART";
    public static final String MOVE = "MOVE";
    public static final String PASS = "PASS";
    public static final String GETBOARD = "GETBOARD";
    /**
     * BOARD EEEEEEBEEWWEE... 3 16     (board repr., black captives , white captives)
     * The black captives are the black stones captured by white.
     * The white captives are the white stones captured by black.
     */
    public static final String BOARD = "BOARD";
    public static final String GAMEOVER = "GAMEOVER";
    /**
     * Client -> Server QUIT: Close socket, game, everything.
     *      If this happens while the quitting client is in the game,
     *      the other player receives GAMEOVER VICTORY;
     * Client -> Server STOPGAME: stop the current game (only allowed when playing a game)
     *      The Server sends GAMEOVER VICTORY/DEFEAT appropriately.
     * Client -> Server CANCEL: only allowed after 'PLAY' or 'CHALLENGE name'
     *      Both the client that sent CANCEL and a possible opponent that is involved
     *      in this actions, receive CANCELLED from the Server;
     */
    public static final String QUIT = "QUIT";
    public static final String CANCEL = "CANCEL";
    public static final String STOPGAME = "STOPGAME";
    public static final String CANCELLED = "CANCELLED";

    /**
     * Strings used to denote color
     */
    public static final String WHITE = "WHITE";
    public static final String BLACK = "BLACK";

    /**
     * Messages for ending game. If two players pass consecutively AND white is the last to pass,
     * the game stops. The server sends GAMEOVER to both clients, followed by either VICTORY,
     * DEFEAT or DRAW.
     */
    public static final String VICTORY = "VICTORY";
    public static final String DEFEAT = "DEFEAT";
    public static final String DRAW = "DRAW";

    /**
     * Representation of the intersections when sending board.
     */
    public static final String W = "W";
    public static final String B = "B";
    public static final String E = "E";

    /**
     * Client -> Server:    CHAT blabla
     * Server -> Client:    CHAT Jan blabla
     *
     * If you CHAT and are not playing a game, all clients that are not playing a
     * game, receive the message. If you are playing a game, only your opponent receives the message.
     *
     * If you are an observer, you cannot chat.
     */
    public static final String CHAT = "CHAT";
    public static final String CHALLENGE = "CHALLENGE";
    public static final String OBSERVER = "OBSERVER";
    public static final String COMPUTERPLAYER = "COMPUTERPLAYER";

    /**
     * Commands for extra credit functionality.
     */

    // CHALLENGE
    /**
     * Example of the use of this part of the protocol:
     * 1. Client1 -> Server:   CHALLENGE
     * 2. Server -> Client1: AVAILABLEPLAYERS Kees
     * 3. Client1 -> Server: CHALLENGE Kees
     * 4. Server -> Client1: YOUVECHALLENGED Kees
     * 5. Server -> Client2: YOURECHALLENGED Jan
     * 6. Client2 -> Server: CHALLENGEACCEPTED
     * 7. Server -> Client1: CHALLENGEACCEPTED
     * 8. Server -> Client1: GAMESTART Kees 9 WHITE
     * 9. Server -> Client2: GAMESTART Jan 9 BLACK
     *
     * Alternatively in step 5. & 6. the answer of Client2 may be CHALLENGEDENIED,
     * and the server will pass this on to Client1.
     *
     * A client can only be challenged by 1 client simultaneously.
     */
    public static final String AVAILABLEPLAYERS = "AVAILABLEPLAYERS";
    public static final String YOUVECHALLENGED = "YOUVECHALLENGED";
    public static final String YOURECHALLENGED = "YOURECHALLENGED";
    public static final String CHALLENGEACCEPTED = "CHALLENGEACCEPTED";
    public static final String CHALLENGEDENIED = "CHALLENGEDENIED";

    // OBSERVER
    public static final String OBSERVE = "OBSERVE";
    public static final String NOGAMESPLAYING = "NOGAMESPLAYING";
    public static final String CURRENTGAMES = "CURRENTGAMES";
    public static final String OBSERVEDGAME = "OBSERVEDGAME";

    // AI
    /**
     * Client -> Server PRACTICE: The client can say PRACTICE with or without a stategy.
     * if the server supports multiple strategies, it sends:
     * Server -> Client AVAILABLESTRATEGIES <strategy> <strategy>
     * The Server sends a GAMESTART COMPUTER <boardsize> <COLOR>
     */
    public static final String PRACTICE = "PRACTICE";
    public static final String AVAILABLESTRATEGIES = "AVAILABLESTRATEGIES";
    public static final String COMPUTER = "COMPUTER";
    /**
     * HINT and GETHINT are optional. A client may send a GETHINT command,
     * the server can respond with HINT 6 0, in the same format as a move.
     */
    public static final String GETHINT = "GETHINT";
    public static final String HINT = "HINT";

    /**
     * If a client sends a command to the server, and the server cannot produce (one of the) expected response(s), it
     * will send the command FAILURE, followed by one of the errors listed below.
     *
     * Example:
     * Server -> Client: FAILURE NotApplicableCommand
     */
    public static final String FAILURE = "FAILURE";

    /**
     * Command not part of protocol, connection is closed.
     */
    public static final String UNKNOWNCOMMAND = "UnknownCommand";

    /**
     * Command is not applicable in the current state, e.g. trying to register a second time,
     * or trying to MOVE while not playing.
     */
    public static final String NOTAPPLICABLECOMMAND = "NotApplicableCommand";

    /**
     * This command can only be used with a number of arguments, too few arguments were provided.
     */
    public static final String ARGUMENTSMISSING = "ArgumentsMissing";

    /**
     * Command is part of the protocol, but is not supported by the server.
     */
    public static final String NOTSUPPORTEDCOMMAND = "NotSupportedCommand";

    /**
     * If a client is non-responsive for more than the agreed TIMEOUTSECONDS, the server broadcasts this failure to
     * the clients and shuts down the game. We define a client as non-responsive if it does not respond for
     * TIMEOUTSECONDS while reaction is expected (e.g. when client is expected to move).
     * The player that lets the timeouttime pass.
     */
    public static final String TIMEOUTEXCEEDED = "TimeOutExceeded";

    public static final String INVALIDNAME = "InvalidName";
    public static final String NAMETAKEN = "NameTaken";
    public static final String NAMENOTALLOWED = "NameNotAllowed";
    public static final String INVALIDMOVE = "InvalidMove";
    public static final String NOTYOURTURN = "NotYourTurn";
    /**
     * If an argument is not valid for the given situation, the server can send a FAILURE IllegalArgument.
     */
    public static final String ILLEGALARGUMENT = "IllegalArgument";
    public static final String OTHERPLAYERCANNOTCHAT = "OtherPlayerCannotChat";
    public static final String PLAYERNOTAVAILABLE = "PlayerNotAvailable";
    public static final String GAMENOTPLAYING = "GameNotPlaying";
}