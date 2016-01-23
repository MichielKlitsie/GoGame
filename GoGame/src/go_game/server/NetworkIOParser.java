package go_game.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.protocol.Constants2;
import go_game.protocol.Constants3;

public class NetworkIOParser implements Constants3 {

	// Instance variables ----------------------------
	private String inputString;
	private String outputString;
	private String nameChallenger; 
	private String nameChallenged; 
	private ClientHandler clientHandler;
	private Server server;
	private static final String BOARDSIZE = "9";

	// Constructor ----------------------------
	/**
	 * Constructor.
	 */
	public NetworkIOParser(ClientHandler clientHandler, Server server) {
		this.inputString = "";
		this.outputString = "";
		this.clientHandler = clientHandler;
		this.server = server;
	}

	// Methods and commands ----------------------------
	/**
	 * Parse the incoming input of the client handler and call the corresponding functionality on the server side.
	 * For all commands, see the interface Constant3.java
	 * @param inputString
	 * @return outputString (optional)
	 */
	public String parseInput(String inputString) {
		int d = 0;
		String outputCommand = "Command not recognized";

		// Split commando from arguments
		String[] stringParts = inputString.split(DELIMITER);
		String command = stringParts[0].trim().toUpperCase();
		int amountArgs = stringParts.length - 1;
		System.out.println("Parsing: '" + command + "' with " + amountArgs + " arguments.");

		// Perform switch case...
		switch (command) {

		// GENERAL commands ------------------------------------------
		case VERSION:
			clientHandler.sendMessageToClient("Current protocol version: " + VERSION);
			break;
		case "SERVER_PORT":
			clientHandler.sendMessageToClient("Current server port: " + SERVER_PORT);
			break;
		case "TIMEOUTSECONDS":
			clientHandler.sendMessageToClient("Current you have " + TIMEOUTSECONDS + " seconds until time-out");
			break;


			// BASIC LOBBY commands ------------------------------------------
		case NEWPLAYER:
			//TODO: PROTOCOL NEWPLAYER
			// Create a player from the commands
			String name = "";
			if (amountArgs == 0) {
				name = "NoNamePlayer";
			} else if (amountArgs == 1) {
				name = stringParts[1];
			} else {
				// Do something with not enough arguments
			}

			outputCommand = NEWPLAYERACCEPTED;
			System.out.println(outputCommand + " : " + name);
			break;

		case NEWPLAYERACCEPTED:
			//TODO: PROTOCOL NEWPLAYERACCEPTED
			break;

		case GETOPTIONS: 
			String optionsMenu = clientHandler.getOptions();
			outputCommand = "OPTIONS";
			System.out.println(outputCommand + " : " + optionsMenu);
			break;

		case OPTIONS:
			//TODO: PROTOCOL OPTIONS
			break;

		case PLAY:
			//TODO: PROTOCOL RANDOM PLAY
			int amountOtherPlayers = 1;
			if (amountOtherPlayers < 1) {
				outputCommand = WAITFOROPPONENT;
			} else if (amountOtherPlayers > 1) {
				// Select other player and get name
				//					Player p1 = new HumanPlayer("Pies", Mark.OO);
				//					Player p2 = new HumanPlayer("Wimpie", Mark.XX);
				//outputCommand = BOARDSIZE;
			}				
			break;

		case WAITFOROPPONENT:
			//TODO: PROTOCOL WAITFOROPPONENT
			clientHandler.sendMessageToClient("Wait for your opponent to make a move"); 
			break;

		case GAMESTART: 
			if (amountArgs == 3) {
				// Parse arguments
				String namePlayer = stringParts[1];
				int boardDimension = Integer.parseInt(stringParts[2]);
				String mark = stringParts[3];
				System.out.println("\nGame started with name " + namePlayer + 
						" on a board of size " + boardDimension + " as mark " + mark);


				if (namePlayer.equals(COMPUTER)) {
					// Set a computer player
					// Instead of using the clientHandler from the other player, the last input is the players own clientHandler
					clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, clientHandler);
				} else {
					// RESPONSIBILITY ON THE CHALLENGED SIDE TO START THE GAME, THUS FIND THE CHALLENGER (AGAIN)
					List<ClientHandler> availablePlayers = this.server.getPlayers();
					outerloop:
						for (ClientHandler temp : availablePlayers) {
							// temp is the challenger
							if(temp.getClientName().trim().equals(nameChallenger)) {
								clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, temp);
								break outerloop;
							}
						}
					
				}
			} else {
				// Do Nothing
				clientHandler.sendMessageToClient(ARGUMENTSMISSING);
			}


			break;

			// BASIC GAME commands ------------------------------------------


		case MOVE:
			//				 * - Client -> Server:  MOVE 3 4                        (x, y:  0 <= x && x <= boardsize)
			//			     * - Server -> Client:  MOVE BLACK 3 4
			//			     * - Client -> Server:  MOVE PASS;
			int xCo = -1;
			int yCo = -1;
			int choiceIndex = -999;
			boolean isValidInput = false;

			// Initialize regex patterns
			String patternOneLetter = "[a-zA-Z]";
			String patternOneNumber = "[0-9]+";
			String patternLetterNumber = "[a-zA-Z]\\s[0-9]+";
			String patternNumberNumber = "^[0-9]+\\s[0-9]+$";

			// INPUT IS ONE ARGUMENT
			if (amountArgs == 1) {
				//TODO: GETTING THE PASS COMMAND RIGHT
				String singleInputArg = stringParts[1].trim();
				if (singleInputArg.equalsIgnoreCase("pass")) {
					System.out.println("Parsing the pass move command");
					choiceIndex = -1;
					clientHandler.sendMessageToClient(PASS);
					isValidInput = true;
				} else if (singleInputArg.length() == 1) {
					System.out.println("Parsing the one argument move command");
					if (singleInputArg.matches(patternOneLetter)) {
						// INPUT SINGLE LETTER
						String noValidInput = "A single letter is no valid input... \n";
						System.out.println(noValidInput);
						clientHandler.sendMessageToClient(INVALIDMOVE);
					} else if (singleInputArg.matches(patternOneNumber)) {
						// INPUT INDEX
						choiceIndex = Integer.parseInt(singleInputArg) - 1;
						isValidInput = true;
					} else {
						// OR ELSE....
						String noValidInput = "No valid input, try again... \n";
						System.out.println(noValidInput);
						clientHandler.sendMessageToClient(INVALIDMOVE);
					}
				}

				// If input is length 2, check if (number,number) or (letter, number)
			} else if (amountArgs == 2) {
				System.out.println("Parsing the two argument move command");
				String firstArg = stringParts[1].trim();
				String secondArg = stringParts[2].trim();
				// Check the input versus the patterns
				// Pattern 1: Number Number
				if (firstArg.matches(patternOneNumber) && secondArg.matches(patternOneNumber)) {	
					xCo = Integer.parseInt(firstArg);
					yCo = Integer.parseInt(secondArg);
					isValidInput = true;

					// Pattern 2: Letter Number	
				} else if (firstArg.matches(patternOneLetter) && secondArg.matches(patternOneNumber)) {
					// Change (letter,number) to (number,number)
					String rowLowerCase = firstArg.toLowerCase();
					char[] ch = rowLowerCase.toCharArray();
					xCo = ch[0] - 'a' + 1;
					yCo = Integer.parseInt(secondArg);
					//						choice = board.index(rowInt - 1, Integer.parseInt(secondArg) - 1);
					isValidInput = true;
				} else {
					String noValidInput = "No valid input, try again... \n";
					System.out.println(noValidInput);
					clientHandler.sendMessageToClient(INVALIDMOVE);
				}
				// If the user inputted a pass, continue with value -1	
			} else if (amountArgs == 3) {
				//				String strMark = stringParts[1];
				//				int xCo = Integer.parseInt(stringParts[2].trim());
				//				int yCo = Integer.parseInt(stringParts[3].trim());
				System.out.println("Why is this neccesary?");
			} else {
				clientHandler.sendMessageToClient(ARGUMENTSMISSING);
			}


			if(isValidInput) {
				clientHandler.sentParsedMoveToGoGameServer(xCo, yCo);
			}



			break;
		case PASS:
			//TODO: PROTOCOL COMMAND PASS
			break;

		case GETBOARD: 
			// Obtain the string representation of the board
			String stringBoard = clientHandler.getProtocolStringRepresentationBoard();
			System.out.println("Board status sent: " + stringBoard);
			clientHandler.sendMessageToClient(BOARD + DELIMITER + stringBoard);
			break;

		case BOARD: 
			//TODO: PROTOCOL COMMAND BOARD
			break;

		case QUIT: d = 1; break;
		case GAMEOVER: d = 1; break;


		// Arguments
		//			case BOARDSIZE : d = 1; break;
		//			case COLOR: d = 1; break;
		case VICTORY: d = 1; break;
		case DEFEAT: d = 1; break;

		// Options ---------------------------------------------------------------
		case CHAT: 
			String chatMessage = clientHandler.getClientName() + ": " + 
					inputString.substring(command.length());
			//				System.out.println("Command broadcasted to all: " + chatMessage);
			server.broadcast(chatMessage);
			break;

			// CHALLENGE ---------------------------------------------------------------
		case CHALLENGE:
			// SHOW A LIST OF AVAILABLE CHALLENGERS
			if (amountArgs == 0) {
				// SEND BACK
				outputCommand = AVAILABLEPLAYERS;
				// GET a string OF AVAILABLE PLAYERS
				String s = getStringOfAvailablePlayers();
				clientHandler.sendMessageToClient(outputCommand);
				clientHandler.sendMessageToClient(s);

				// CHALLENGE A SPECIFIC PLAYER
			} else if (amountArgs == 1) {
				String nameToBeChallenged = stringParts[1].trim();
				String nameChallenger = clientHandler.getClientName().trim();
				boolean challengerAvailable = false;

				// Check for own name. You cannot challenge yourself...
				if (nameToBeChallenged.equals(nameChallenger)) {
					String challengeSelfMessage = "You cannot challenge yourself, try again.";
					clientHandler.sendMessageToClient(challengeSelfMessage);
					break;
				}

				// send message to the player who corresponds to the input name
				List<ClientHandler> availablePlayers = this.server.getPlayers();
				outerloop:
					for (ClientHandler temp : availablePlayers) {
						// When found, send a message ...
						if (temp.getClientName().trim().equals(nameToBeChallenged)) {

							// CHALLENGER SIDE
							clientHandler.sendMessageToClient(YOUVECHALLENGED + DELIMITER + nameToBeChallenged);
							clientHandler.setPendingChallengeStatus(true);

							// TO BE CHALLENGED SIDE
							temp.sendMessageToClient(YOURECHALLENGED + DELIMITER + nameChallenger);
							temp.sendMessageToClient(": You are challenged by '" + nameChallenger + "', respond with '" + CHALLENGEACCEPTED + "' or '" + CHALLENGEDENIED + "'.");
							challengerAvailable = true;

							// ... and set the duo on the list on the server
							server.addChallengePartners(nameChallenger, nameToBeChallenged);

							break outerloop;
						} 
					}

				if (!challengerAvailable) {
					String notAvailableMessage = "The player you inputted is not available.";
					System.out.println(notAvailableMessage);
					clientHandler.sendMessageToClient(notAvailableMessage);
					clientHandler.sendMessageToClient(PLAYERNOTAVAILABLE);
				}
				
				clientHandler.setPendingChallengeStatus(true);

			} else {
				// More than 1 argument
				clientHandler.sendMessageToClient(ARGUMENTSMISSING);
			}; 
			break;

			// USELESS COMMAND NOW.....
		case YOUVECHALLENGED: 
			if (amountArgs == 0) {
				outputCommand = ARGUMENTSMISSING;
				clientHandler.sendMessageToClient(outputCommand);				
			} else if (amountArgs == 1) {
				String waitMessage = "Waiting for a response from challenged player or type 'q' to withdraw..."; 
				System.out.println(waitMessage);
				clientHandler.sendMessageToClient(waitMessage);


			} else {
				// More than 1 argument
				clientHandler.sendMessageToClient(ARGUMENTSMISSING);
			}
			break;

		case YOURECHALLENGED:
			if (amountArgs == 0) {
				outputCommand = ARGUMENTSMISSING;
				clientHandler.sendMessageToClient(outputCommand);				
			} else if (amountArgs == 1) {
				String challengedMessage = "You are challenged by" + stringParts[1] + 
						". Respond with '" + CHALLENGEACCEPTED + 
						"' or '" + CHALLENGEDENIED + "'"; 
				clientHandler.sendMessageToClient(challengedMessage);
			} else {
				// More than 1 argument
				clientHandler.sendMessageToClient(ARGUMENTSMISSING);
			}
			break;

		case AVAILABLEPLAYERS: 
			String s = getStringOfAvailablePlayers();
			clientHandler.sendMessageToClient(outputCommand);
			clientHandler.sendMessageToClient(s); 
			break;

		case CHALLENGEACCEPTED: 


			if (clientHandler.getPendingChallengeStatus()) {
				// This client must be the challenger and get the message from the server

				// REMOVE self from lobby list
				// GAMESTART
				System.out.println("Oh jeahhhhh");
				String nameChallenger = clientHandler.getClientName().trim(); 
				String gameArgs = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
				clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
				//
			} else {
				// THIS IS WHAT ACTUALLY HAPPENS

				// This client must be the one challenged and in response sending the CHALLENGE ACCEPTED to the server
				//Get the pending challenger; 
				HashMap<String, String> challengePartners = server.getChallengePartners();
				nameChallenged = clientHandler.getClientName().trim();
				nameChallenger = challengePartners.get(nameChallenged);
				List<ClientHandler> availablePlayers = this.server.getPlayers();
				outerloop:
					for (ClientHandler temp : availablePlayers) {
						// temp is the challenger
						if(temp.getClientName().trim().equals(nameChallenger)) {
							System.out.println("Starting match with " + temp.getClientName());

							//Send message to the challenger
							temp.sendMessageToClient(CHALLENGEACCEPTED);
							temp.sendMessageToClient("\nThe challenge has been accepted, get ready for the match!");

							// BROADCAST TO PEOPLE IN LOBBY TWO PLAYERS ARE LEAVING LOBBY TO PLAY A GAME
							server.broadcast("\n[" + nameChallenger + " and " + nameChallenged + " are leaving the lobby to play a game]\n");


							// GAMESTART TO BE CHALLENGED
							System.out.println(clientHandler.getClientName() + ": Challenge status" + clientHandler.getPendingChallengeStatus());
							String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + WHITE; 
							clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);

							// GAMESTART CHALLENGER
							System.out.println(temp.getClientName() + ": Challenge status" + temp.getPendingChallengeStatus());
							String gameArgs2 = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
							temp.sendMessageToClient(GAMESTART + DELIMITER + gameArgs2);

							// GAMESTART TO SERVER
							clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);

							// Start the game on a seperate thread... 
							//							clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, temp);

							break outerloop;
						}
					}
			}
		break; 

		case CHALLENGEDENIED:
			HashMap<String, String> challengePartners = server.getChallengePartners();

			// PARSED BY THE CHALLENGER
			if (clientHandler.getPendingChallengeStatus()) { 
			nameChallenger = clientHandler.getClientName().trim();
			nameChallenged = "";
			extraloop:
				for (Entry<String, String> entry : challengePartners.entrySet()) {
					if ( nameChallenger.equals( entry.getValue() ) ) {
						nameChallenged = entry.getKey().trim();
						clientHandler.sendMessageToClient("Your challenge has been denied by " + nameChallenged + ", please try again!");
						break extraloop;
					}
				}
			
			clientHandler.setPendingChallengeStatus(false);
			challengePartners.remove(nameChallenged);
			
			} else {
				// PARSED BY THE PERSON BEING CHALLENGED
				nameChallenged = clientHandler.getClientName().trim();
				nameChallenger = challengePartners.get(nameChallenged);
				
				List<ClientHandler> availablePlayers = this.server.getPlayers();
				outerloop:
					for (ClientHandler temp : availablePlayers) {
						// temp is the challenger
						if(temp.getClientName().trim().equals(nameChallenger)) {
							temp.sendMessageToClient(CHALLENGEDENIED);
							temp.sendMessageToServer(CHALLENGEDENIED);
							clientHandler.sendMessageToClient("Your denial has been sent to " + nameChallenger);
							break outerloop;
						}
					}
			}
			break;

			// OBSERVER ------------------------------------------------------------------
		case OBSERVER: 
			
			d = 1; break; //OBSERVER";
		case COMPUTERPLAYER: d = 1; break; //COMPUTERPLAYER";
		//			case BOARDSIZES: d = 1; break; //BOARDSIZES";

		// Extra commands for the different options
		// CHAT
		// -

		// OBSERVER
		case NOGAMESPLAYING: d = 1; break; //NOGAMESPLAYING";
		case CURRENTGAMES: d = 1; break; //CURRENTGAMES";
		case OBSERVEDGAME: d = 1; break; //OBSERVEDGAME";

		// AI
		case PRACTICE: 
//			outputCommand = GAMESTART;
			nameChallenged = COMPUTER; 
			String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + BLACK;
//			clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
			clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);
			break; //PRACTICE";
		case COMPUTER: d = 1; break; //COMPUTER";

		// Errors
		case FAILURE: d = 1; break; //FAILURE";

		//			case UNKOWNCOMMAND: d = 1; break; //UnkownCommand";
		//			case WRONGCOMMAND: d = 1; break; //WrongCommmand";
		case ARGUMENTSMISSING: d = 1; break; //ArgumentsMissing";
		case INVALIDNAME: d = 1; break; //InvalidName";
		case NAMETAKEN: d = 1; break; //NameTaken";
		case NAMENOTALLOWED: d = 1; break; //NameNotAllowed";
		case INVALIDMOVE: d = 1; break; //InvalidMove";
		case NOTYOURTURN: d = 1; break; //NotYourTurn";
		case ILLEGALCHARACTERS: d = 1; break; //IllegalCharacters";
		case OTHERPLAYERCANNOTCHAT: d = 1; break; //OtherPlayerCannotChat";
		case PLAYERNOTAVAILABLE: d = 1; break; //PlayerNotAvailable";
		case GAMENOTPLAYING: d = 1; break; //GameNotPlaying";

		// REMOVING THREAD
		case "CLIENTEXIT":
			this.clientHandler.shutdown();
			System.out.println("\nPlayers left in lobby:\n");
			System.out.println(getStringOfAvailablePlayers());
			break;
		case "EXIT":
			this.clientHandler.shutdown();
			System.out.println("\nPlayers left in lobby:\n");
			System.out.println(getStringOfAvailablePlayers());
			break;

			// NOT RECOGNISED	
		default: 
			System.out.println("Not parsing");
			outputCommand = UNKNOWNCOMMAND;
			System.out.println("Command sent to " + clientHandler.getClientName() + ": " + outputCommand);
			clientHandler.sendMessageToClient(outputCommand + " : Use a known command, see 'GETOPTIONS'.");
			break;
		}
		return outputCommand;

	}

	private String getStringOfAvailablePlayers() {
		List<ClientHandler> availablePlayers = this.server.getPlayers();
		String s = "";
		for (int i = 0; i < availablePlayers.size(); i++) {
			ClientHandler temp = availablePlayers.get(i);
			// Discard own name
			if (temp.getClientName().trim() != this.clientHandler.getClientName()) {
				s = s + "\n" + (i + 1) + ". " + temp.getClientName();
			}
		}
		return s;
	}


}
