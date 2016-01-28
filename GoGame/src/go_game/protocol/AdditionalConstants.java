package go_game.protocol;

public interface AdditionalConstants {
	 // General

	public static final String SMARTSTRATEGY = "SMARTSTRATEGY";
	public static final String MIRRORSTRATEGY = "MIRRORSTRATEGY";
	public static final String CUTTINGSTRATEGY = "CUTTINGSTRATEGY";
	public static final String RANDOMSTRATEGY = "RANDOMSTRATEGY";
	public static final String GETALLTHREADS = "GETALLTHREADS";
	// ADDITIONAL COMMANDS
	public static final String GETSTATUS = "GETSTATUS";
	public static final String CLIENTEXIT = "CLIENTEXIT";
	public static final String EXIT = "EXIT";
	public static final String CHANGENAME = "CHANGENAME";
	public static final String STATUS = "STATUS";

	// Linebreak
	public static final String LINEBREAK = "//----------------------------\n";
	

	// OPTIONS MENUs corresponding to different states
	// TODO: FINALIZE THE OPTION MENUS
	public static final String OPTIONSMENULOBBY = "Options menu lobby:\n" +
			"1. PLAY: Play a game agains a random person.\n" + 
			"2. CHALLENGE/AVAILABLEPLAYERS: Get a list of the people in the lobby.\n" +
			"3. CHALLENGE <namePlayer>: Challenge a specific player in the lobby.\n" +
			"4. PRACTICE: Practice against a computer player.\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"7. GETSTATUS: Get your current status. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	public static final String OPTIONSMENUPLAYING = "Options menu playing:\n" +
			"1. MOVE: Play a move in the game. Input as 'MOVE int row, int column', 'MOVE char row, int column', 'MOVE index' or 'MOVE PASS'.\n" + 
			"2. PASS: Challenge a specific player.\n" + 
			"5. CHAT: Send a message to the opponent players.\n" +
			"4. ...: ....\n" + 
			"x. GETOPTIONS: Get this options menu.\n";
	public static final String OPTIONSMENUPENDINGCHALLENGE = "Options menu pending challenge:\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	public static final String OPTIONSMENUWAITINGMOVE = "Options menu waiting on move:\n" +
			"5. CHAT: Send a message to the opponent players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
	public static final String OPTIONSMENUOBSERVING = "Options menu observing:\n" +
			"5. CHAT: Send a message to the playing players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
	public static final String OPTIONSMENUWAITINGROOM = "Options menu waiting room:\n" +
			"1. NEWPLAYER <name>: Set a new name.\n" +
			"2. QUIT: Exit the server.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
}
