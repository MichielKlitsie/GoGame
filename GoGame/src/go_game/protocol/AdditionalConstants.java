/*
 * 
 */
package go_game.protocol;

// TODO: Auto-generated Javadoc
/**
 * The Interface AdditionalConstants.
 */
public interface AdditionalConstants {
	 // General

	/** The Constant SMARTSTRATEGY. */
 	public static final String SMARTSTRATEGY = "SMARTSTRATEGY";
	
	/** The Constant MIRRORSTRATEGY. */
	public static final String MIRRORSTRATEGY = "MIRRORSTRATEGY";
	
	/** The Constant CUTTINGSTRATEGY. */
	public static final String CUTTINGSTRATEGY = "CUTTINGSTRATEGY";
	
	/** The Constant RANDOMSTRATEGY. */
	public static final String RANDOMSTRATEGY = "RANDOMSTRATEGY";
	
	/** The Constant GETALLTHREADS. */
	public static final String GETALLTHREADS = "GETALLTHREADS";
	
	/** The Constant GETSTATUS. */
	// ADDITIONAL COMMANDS
	public static final String GETSTATUS = "GETSTATUS";
	
	/** The Constant CLIENTEXIT. */
	public static final String CLIENTEXIT = "CLIENTEXIT";
	
	/** The Constant EXIT. */
	public static final String EXIT = "EXIT";
	
	/** The Constant CHANGENAME. */
	public static final String CHANGENAME = "CHANGENAME";
	
	/** The Constant STATUS. */
	public static final String STATUS = "STATUS";

	/** The Constant LINEBREAK. */
	// Linebreak
	public static final String LINEBREAK = "//----------------------------\n";
	

	// OPTIONS MENUs corresponding to different states
	/** The Constant OPTIONSMENULOBBY. */
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
	
	/** The Constant OPTIONSMENUPLAYING. */
	public static final String OPTIONSMENUPLAYING = "Options menu playing:\n" +
			"1. MOVE: Play a move in the game. Input as 'MOVE int row, int column', 'MOVE char row, int column', 'MOVE index' or 'MOVE PASS'.\n" + 
			"2. PASS: Challenge a specific player.\n" + 
			"5. CHAT: Send a message to the opponent players.\n" +
			"4. ...: ....\n" + 
			"x. GETOPTIONS: Get this options menu.\n";
	
	/** The Constant OPTIONSMENUPENDINGCHALLENGE. */
	public static final String OPTIONSMENUPENDINGCHALLENGE = "Options menu pending challenge:\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	
	/** The Constant OPTIONSMENUWAITINGMOVE. */
	public static final String OPTIONSMENUWAITINGMOVE = "Options menu waiting on move:\n" +
			"5. CHAT: Send a message to the opponent players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
	
	/** The Constant OPTIONSMENUOBSERVING. */
	public static final String OPTIONSMENUOBSERVING = "Options menu observing:\n" +
			"5. CHAT: Send a message to the playing players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
	
	/** The Constant OPTIONSMENUWAITINGROOM. */
	public static final String OPTIONSMENUWAITINGROOM = "Options menu waiting room:\n" +
			"1. NEWPLAYER <name>: Set a new name.\n" +
			"2. QUIT: Exit the server.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
}
