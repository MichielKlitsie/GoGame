package go_game.protocol;


public interface Constants {
	// Delimiters
	public static final String DELIMITER = " ";
	public static final String ARG_DELIMITER = ";";

	// General
	// Commands
	public static final String NEWPLAYER  = "NEWPLAYER";
	public static final String OPTIONS = "OPTIONS";
	public static final String PLAY = "PLAY";
	public static final String WAITFOROPPENENT = "WAITFOROPPONENT";
	public static final String GAMESTART = "GAMESTART";
	public static final String MOVE = "MOVE";
	public static final String VALIDMOVE = "VALIDMOVE";
	public static final String OTHERPLAYERMOVED = "OTHERPLAYERMOVED";
	public static final String GETBOARD = "GETBOARD";
	public static final String BOARD = "BOARD";
	public static final String QUIT = "QUIT";
	public static final String GAMEOVER = "GAMEOVER";

	// Arguments
	public static final String BOARDSIZE = "BOARDSIZE";
	public static final String COLOR = "COLOR";
	public static final String VICTORY = "VICTORY";
	public static final String DEFEAT = "DEFEAT";

	// Options
	public static final String CHAT = "CHAT";
	public static final String CHALLENGE = "CHALLENGE";
	public static final String OBSERVER = "OBSERVER";
	public static final String COMPUTERPLAYER = "COMPUTERPLAYER";
	public static final String BOARDSIZES = "BOARDSIZES";

	// Extra commands for the different options
	// CHAT
	// -

	// CHALLENGE
	public static final String AVAILABLEPLAYERS = "AVAILABLEPLAYERS";
	public static final String CHALLENGEACCEPTED = "CHALLENGEACCEPTED";
	public static final String CHALLENGEDENIED = "CHALLENGEDENIED";

	// OBSERVER
	public static final String NOGAMESPLAYING = "NOGAMESPLAYING";
	public static final String CURRENTGAMES = "CURRENTGAMES";
	public static final String OBSERVEDGAME = "OBSERVEDGAME";

	// AI
	public static final String PRACTICE = "PRACTICE";
	public static final String COMPUTER = "COMPUTER";

	// Errors
	public static final String FAILURE = "FAILURE";

	public static final String UNKOWNCOMMAND = "UnkownCommand";
	public static final String WRONGCOMMAND = "WrongCommmand";
	public static final String ARGUMENTSMISSING = "ArgumentsMissing";
	public static final String INVALIDNAME = "InvalidName";
	public static final String NAMETAKEN = "NameTaken";
	public static final String NAMENOTALLOWED = "NameNotAllowed";
	public static final String INVALIDMOVE = "InvalidMove";
	public static final String NOTYOURTURN = "NotYourTurn";
	public static final String ILLEGALCHARACTERS = "IllegalCharacters";
	public static final String OTHERPLAYERCANNOTCHAT = "OtherPlayerCannotChat";
	public static final String PLAYERNOTAVAILABLE = "PlayerNotAvailable";
	public static final String GAMENOTPLAYING = "GameNotPlaying";
}

