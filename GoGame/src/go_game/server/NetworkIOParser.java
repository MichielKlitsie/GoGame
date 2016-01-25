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

	// STATES OF THE CORRESPONDING CLIENTHANDLER
	private boolean isPlaying;
	private boolean isInLobby;
	private boolean isPendingChallenge;
	private boolean isWaitingOnMove;
	private boolean isAlreadyChallenged;
	// PREMADE STRINGS
	private String pendingChallengeMessage = "You are waiting on a challenge response, do not try to do anything crazy.\n";
	

	// ADDITIONAL COMMANDS
	private static final String GETSTATUS = "GETSTATUS";
	private static final String CLIENTEXIT = "CLIENTEXIT";
	private static final String EXIT = "EXIT";
	
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

		// GET THE STATE OF THE CORRESPONDING CLIENTHANDLER
		this.isPlaying = clientHandler.getIsPlaying();
		this.isInLobby = clientHandler.getIsInLobby();
		this.isPendingChallenge = clientHandler.getPendingChallengeStatus();
		this.isWaitingOnMove = clientHandler.getIsWaiting();
		this.isAlreadyChallenged = clientHandler.getIsAlreadyChallenged();

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
			if (this.isPendingChallenge) {
				clientHandler.sendMessageToClient(clientHandler.getOptionsPendingChallenge());
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(clientHandler.getOptionsLobby());
			} else if (this.isPlaying) {
				clientHandler.sendMessageToClient(clientHandler.getOptionsGame());
			} else if (this.isWaitingOnMove) {
				clientHandler.sendMessageToClient(clientHandler.getOptionsWaitingOnMove());
			} else {
				clientHandler.sendMessageToClient("Your current state is unclear... Sorry!");
			}

			//			outputCommand = "OPTIONS";
			//			System.out.println(outputCommand + " : " + optionsMenu);
			break;

		case OPTIONS:
			//TODO: PROTOCOL OPTIONS
			break;

		case PLAY:
			if (this.isPendingChallenge || this.isPlaying) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
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
			}

		case WAITFOROPPONENT:
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				clientHandler.sendMessageToClient("Wait for your opponent to make a move");
				clientHandler.setIsWaiting(true);
				break;
			}

		case GAMESTART: 
			if (this.isPlaying) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
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
						nameChallenger = clientHandler.getClientName().trim(); 
						nameChallenged = namePlayer;
						clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, clientHandler);
					} else {
						// RESPONSIBILITY ON THE CHALLENGED SIDE TO START THE GAME, THUS FIND THE CHALLENGER (AGAIN)
						List<ClientHandler> availablePlayers = this.server.getAllPlayers();
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
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
				}
				break;
			}


			// ------------------------------------------------------------------
			// BASIC GAME commands ------------------------------------------
			// ------------------------------------------------------------------
		case MOVE:
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				int xCo = -1;
				int yCo = -1;
				int choiceIndex = -999;

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
						//					choiceIndex = -1;
						clientHandler.sentParsedMoveToGoGameServer(PASS);
						//					clientHandler.sendMessageToClient(PASS);
						//					isValidInput = true;

					} else if (singleInputArg.matches(patternOneLetter)) {
						// INPUT SINGLE LETTER
						clientHandler.sendMessageToServer(FAILURE + DELIMITER + INVALIDMOVE);

					} else if (singleInputArg.matches(patternOneNumber)) {
						System.out.println("Parsing the one argument move command");
						// INPUT INDEX
						choiceIndex = Integer.parseInt(singleInputArg) - 1;
						//						isValidInput = true;
						clientHandler.sentParsedMoveToGoGameServer(choiceIndex);

					} else {
						// OR ELSE....
						clientHandler.sendMessageToServer(FAILURE + DELIMITER + INVALIDMOVE);
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
						//					isValidInput = true;
						clientHandler.sentParsedMoveToGoGameServer(xCo, yCo);

						// Pattern 2: Letter Number	
					} else if (firstArg.matches(patternOneLetter) && secondArg.matches(patternOneNumber)) {
						// Change (letter,number) to (number,number)
						String rowLowerCase = firstArg.toLowerCase();
						char[] ch = rowLowerCase.toCharArray();
						xCo = ch[0] - 'a' + 1;
						yCo = Integer.parseInt(secondArg);
						//						choice = board.index(rowInt - 1, Integer.parseInt(secondArg) - 1);
						//					isValidInput = true;
						clientHandler.sentParsedMoveToGoGameServer(xCo, yCo);

					} else {
						clientHandler.sendMessageToServer(FAILURE + DELIMITER + INVALIDMOVE);
					}
					// If the user inputted a pass, continue with value -1	
				} else if (amountArgs >= 3) {
					clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);
				} else {
					clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);
				}
				break;
			}
		case PASS:
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				// Send the MOVE COMMAND INCLUDING PASS
				clientHandler.sendMessageToServer(MOVE + DELIMITER + PASS);
				break;
			}
		
		// TODO: BOARD SENDING....	
		case GETBOARD: 
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				// Obtain the string representation of the board
				String stringBoard = clientHandler.getProtocolStringRepresentationBoard();
				System.out.println("Board status sent: " + stringBoard);
				clientHandler.sendMessageToClient(BOARD + DELIMITER + stringBoard);
				break;
			}
		case BOARD: 
			//TODO: PROTOCOL COMMAND BOARD
			break;

		case QUIT: 
			// <<< HIER GEBLEVEN --------------------------------------------------
			// <<< HIER GEBLEVEN --------------------------------------------------
			// <<< HIER GEBLEVEN --------------------------------------------------
			if (this.isPendingChallenge) {
				// Retract the challenge and change the status
				clientHandler.setPendingChallengeStatus(false);
				clientHandler.setIsInLobby(true);
				clientHandler.sendMessageToClient("Challenge is retracted.\n");
				
				// Sent the message to the person challenged
				HashMap<String, String> challengePartners = server.getChallengePartners();
				nameChallenger = clientHandler.getClientName().trim();
				nameChallenged = "";
				extraloop:
					for (Entry<String, String> entry : challengePartners.entrySet()) {
						if ( nameChallenger.equals( entry.getValue() ) ) {
							nameChallenged = entry.getKey().trim();
							break extraloop;
						}
					}

				List<ClientHandler> availablePlayers = this.server.getAllPlayers();
				outerloop:
					for (ClientHandler temp : availablePlayers) {
						// temp is the challenger
						if(temp.getClientName().trim().equals(nameChallenged)) {
							temp.sendMessageToClient("\nThe challenge has been retracted by " + nameChallenger + "\n");
							temp.setIsAlreadyChallenged(false);
							break outerloop;
						}
					}
				
				// And remove the challenge partners
				challengePartners.remove(nameChallenged);

				break;
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient("Exiting lobby...");
				clientHandler.shutdown();
				break;
			} else if (this.isPlaying) {
				// Step 1: PERSON SENDING QUIT
				clientHandler.sendMessageToClient("Quiting game...");
				// Step 2: SENDING QUIT TO OPPONENT, is that opponent is still playing 
				if (clientHandler.getClientHandlerOpponent().getIsPlaying()) {
					clientHandler.getClientHandlerOpponent().sendMessageToClient("Opponent has quit the game");
					clientHandler.getClientHandlerOpponent().sendMessageToServer(QUIT);	
				}
				clientHandler.setIsPlaying(false);
				clientHandler.setIsInLobby(true);
			}

			// ------------------------------------------------------------------
			// END GAME COMMANDS	
			// ------------------------------------------------------------------
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

			// Get if the player is in the lobby or playing a game...
			if(this.isInLobby) {
				server.broadcast(chatMessage);
			} else if (this.isPlaying) {
				// GET THE OPPONENT
				clientHandler.getClientHandlerOpponent().sendMessageToClient(chatMessage);
			}
			break;

			// ---------------------------------------------------------------
			// CHALLENGE ---------------------------------------------------------------
			// ---------------------------------------------------------------
		case CHALLENGE:
			if (this.isPlaying || this.isPendingChallenge) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				// SHOW A LIST OF AVAILABLE CHALLENGERS
				if (amountArgs == 0) {
					// SEND BACK
					outputCommand = AVAILABLEPLAYERS;
					// GET a string OF AVAILABLE PLAYERS
					List<ClientHandler> listPlayersInLobby = getListOfPlayersInLobby();
					String s = "Players in the lobby: \n";
					s = s + createStringOfListPlayers(listPlayersInLobby);
					//				clientHandler.sendMessageToClient(outputCommand);
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
					List<ClientHandler> availablePlayers = this.server.getAllPlayers();
					outerloop:
						for (ClientHandler temp : availablePlayers) {
							// When found, send a message ...
							if (temp.getClientName().trim().equals(nameToBeChallenged)) {

								if (!temp.getIsAlreadyChallenged()) {
									// CHALLENGER SIDE
									clientHandler.sendMessageToServer(YOUVECHALLENGED + DELIMITER + nameToBeChallenged);
									clientHandler.setPendingChallengeStatus(true);
									// Disabling the lobby functionallity
									clientHandler.setIsInLobby(false);

									// TO BE CHALLENGED SIDE
//									temp.sendMessageToClient(YOURECHALLENGED + DELIMITER + nameChallenger);
									temp.sendMessageToClient("You are challenged by '" + nameChallenger + "', respond with '" + CHALLENGEACCEPTED + "' or '" + CHALLENGEDENIED + "'.");
									temp.setIsAlreadyChallenged(true);
									challengerAvailable = true;

									// ... and set the duo on the list on the server
									server.addChallengePartners(nameChallenger, nameToBeChallenged);
								} else {
									clientHandler.sendMessageToClient("PLayer is already challenged by somebody else...");
								}
								break outerloop;
							} 
						}

					if (!challengerAvailable) {
						clientHandler.sendMessageToServer(FAILURE + DELIMITER + PLAYERNOTAVAILABLE);
					}

					clientHandler.setPendingChallengeStatus(true);

				} else {
					// More than 1 argument
					clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);
				}; 
				break;
			}

			// USELESS COMMAND NOW.....
		case YOUVECHALLENGED: 
			if (amountArgs == 0) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);				
			} else if (amountArgs == 1) {
				String waitMessage = "\nWaiting for a response from " + stringParts[1].trim() + " or type 'QUIT' to withdraw...\n"; 
//				System.out.println(waitMessage);
				clientHandler.sendMessageToClient(waitMessage);


			} else {
				// More than 1 argument
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);
			}
			break;

		case YOURECHALLENGED:
			if (amountArgs == 0) {

				clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);				
			} else if (amountArgs == 1) {
				String challengedMessage = "You are challenged by" + stringParts[1] + 
						". Respond with '" + CHALLENGEACCEPTED + 
						"' or '" + CHALLENGEDENIED + "'"; 
				clientHandler.sendMessageToClient(challengedMessage);
			} else {
				// More than 1 argument
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + ARGUMENTSMISSING);
			}
			break;

		case AVAILABLEPLAYERS:
			if (this.isPendingChallenge || this.isPlaying) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				List<ClientHandler> listOfPlayersInLobby = getListOfPlayersInLobby();
				String s = createStringOfListPlayers(listOfPlayersInLobby);
				clientHandler.sendMessageToClient(outputCommand);
				clientHandler.sendMessageToClient(s); 
				break;
			}

		case CHALLENGEACCEPTED: 
			if (this.isPlaying) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				if (this.isPendingChallenge) {
					// This client must be the challenger and get the message from the server

					// REMOVE self from lobby list
					// GAMESTART
					System.out.println("Oh jeahhhhh");
					String nameChallenger = clientHandler.getClientName().trim(); 
					String gameArgs = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
//					clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
					//
				} else {
					// THIS IS WHAT ACTUALLY HAPPENS 

					// This client must be the one challenged and in response sending the CHALLENGE ACCEPTED to the server
					//Get the pending challenger; 
					HashMap<String, String> challengePartners = server.getChallengePartners();
					nameChallenged = clientHandler.getClientName().trim();
					nameChallenger = challengePartners.get(nameChallenged);
					List<ClientHandler> availablePlayers = this.server.getAllPlayers();
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
//								clientHandler.setIsPlaying(true);
//								clientHandler.setIsInLobby(false);

								// GAMESTART PERSON CHALLENGED
//								System.out.println(clientHandler.getClientName() + ": Challenge status - " + this.isPendingChallenge);
								String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + WHITE; 
								clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);

								// GAMESTART CHALLENGER
//								System.out.println(temp.getClientName() + ": Challenge status" + temp.getPendingChallengeStatus());
								String gameArgs2 = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
								temp.sendMessageToClient(GAMESTART + DELIMITER + gameArgs2);

								// GAMESTART TO SERVER FROM THE PERSON CHALLENGED
								clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);

								break outerloop;
							}
						}
				}
				break; 
			}
		case CHALLENGEDENIED:
			if (this.isPlaying) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				HashMap<String, String> challengePartners = server.getChallengePartners();

				// PARSED BY THE CHALLENGER
				if (this.isPendingChallenge) { 
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

					List<ClientHandler> availablePlayers = this.server.getAllPlayers();
					outerloop:
						for (ClientHandler temp : availablePlayers) {
							// temp is the challenger
							if(temp.getClientName().trim().equals(nameChallenger)) {
								temp.sendMessageToClient(CHALLENGEDENIED);
								temp.sendMessageToServer(CHALLENGEDENIED);
								clientHandler.sendMessageToClient("Your denial has been sent to " + nameChallenger);
								clientHandler.setIsAlreadyChallenged(false);
								break outerloop;
							}
						}
				}
				break;
			}


			// ---------------------------------------------------------------
			// OBSERVER ------------------------------------------------------------------
			// ---------------------------------------------------------------

		case OBSERVER: 
			// TODO: GET THE OBSERVER STATUS RIGHT
			d = 1; break; //OBSERVER";
		case NOGAMESPLAYING: d = 1; break; //NOGAMESPLAYING";
		case CURRENTGAMES: 
			if (this.isPlaying) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				List<ClientHandler> listOfPlayersPlaying = getListOfPlayersPlaying();
				String strListPlaying = "";
				if (listOfPlayersPlaying.size() == 0) {
					strListPlaying = "No games are currently played.\n";
				} else {
					strListPlaying = createStringOfListPlayers(listOfPlayersPlaying);
				}
				clientHandler.sendMessageToClient(strListPlaying);
				break;
			}
		case OBSERVEDGAME: d = 1; break; //OBSERVEDGAME";

		// ---------------------------------------------------------------
		// PRACTISE COMMANDS: AI
		// ---------------------------------------------------------------
		case COMPUTERPLAYER: d = 1; break; //COMPUTERPLAYER";
		//			case BOARDSIZES: d = 1; break; //BOARDSIZES";
		case PRACTICE: 
			if (this.isPlaying || this.isPendingChallenge) {
				clientHandler.sendMessageToServer(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				//			outputCommand = GAMESTART;
				nameChallenged = COMPUTER; 
				String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + BLACK;
				//			clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
				clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);
				break; //PRACTICE";
			}
		case COMPUTER: d = 1; break; //COMPUTER";

		// --------------------------------------------------------------------
		// FAILURES AND Errors------------------------------------------------
		// --------------------------------------------------------------------
		case FAILURE: 
			String errorType = stringParts[1].trim();
			//			clientHandler.sendMessageToClient(errorType);
			//			clientHandler.sendMessageToServer(errorType);
			if (errorType.equalsIgnoreCase(NOTAPPLICABLECOMMAND)) {
				clientHandler.sendMessageToClient("This command is not applicable in your current state.\n");
			} else if (errorType.equalsIgnoreCase(UNKNOWNCOMMAND)) {
				clientHandler.sendMessageToClient("This is an unknown command. Please use a known command, see 'GETOPTIONS'.\n");
			} else if (errorType.equalsIgnoreCase(ARGUMENTSMISSING)) {
				clientHandler.sendMessageToClient("This command comes with other arguments.\n");
			} else if (errorType.equalsIgnoreCase(NOTSUPPORTEDCOMMAND)) {
				clientHandler.sendMessageToClient("This command is not supported by the server.\n");
			} else if (errorType.equalsIgnoreCase(INVALIDNAME)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(NAMETAKEN)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(NAMENOTALLOWED)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(INVALIDMOVE)) {
				clientHandler.sendMessageToClient("No valid input, try again... \n"); 
			} else if (errorType.equalsIgnoreCase(NOTYOURTURN)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(ILLEGALCHARACTERS)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(OTHERPLAYERCANNOTCHAT)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(PLAYERNOTAVAILABLE)) {
				clientHandler.sendMessageToClient("The player you inputted is (currently) not available."); 
			} else if (errorType.equalsIgnoreCase(GAMENOTPLAYING)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			}

			break; 

			// ADDITIONAL COMMANDS
		case GETSTATUS:
			String statusString = "Current status:" + 
					"\nIn the lobby: " + isInLobby +
				"\nSent a challenge: " + isPendingChallenge +
				"\nRecieved a challenge: " + isAlreadyChallenged + 
				"\nPlaying a game: " + isPlaying +
				"\nWaiting for opponent: " + isWaitingOnMove + "\n";
			clientHandler.sendMessageToClient(statusString);
			break;
			// REMOVING THREAD
		case CLIENTEXIT:
			this.clientHandler.shutdown();
			System.out.println("\nPlayers left in lobby:\n");
			System.out.println(getListOfPlayersInLobby());
			break;
		case EXIT:
			this.clientHandler.shutdown();
			System.out.println("\nPlayers left in lobby:\n");
			System.out.println(getListOfPlayersInLobby());
			break;

			// NOT RECOGNISED	
		default: 
			//			System.out.println("Not parsing");
			//			outputCommand = UNKNOWNCOMMAND;
			//			System.out.println("Command sent to " + clientHandler.getClientName() + ": " + outputCommand);
			clientHandler.sendMessageToServer(FAILURE + DELIMITER + UNKNOWNCOMMAND);	
			break;
		}
		return outputCommand;

	}

	// ---------------------------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------------------------
	private List<ClientHandler> getListOfPlayersInLobby() {
		List<ClientHandler> availablePlayers = this.server.getPlayersInLobby();
		return availablePlayers;
	}

	private List<ClientHandler> getListOfPlayersPlaying() {
		List<ClientHandler> playingPlayers = this.server.getPlayersPlaying();
		return playingPlayers;
	}

	// String creation of a list of the clientHandlers of the players
	private String createStringOfListPlayers(List<ClientHandler> listClientHandlerPlayers) {
		String s = "";
		for (int i = 0; i < listClientHandlerPlayers.size(); i++) {
			ClientHandler temp = listClientHandlerPlayers.get(i);
			// Discard own name
			if (temp.getClientName().trim() != this.clientHandler.getClientName()) {
				s = s + "\n" + (i + 1) + ". " + temp.getClientName();
			}
		}
		return s;
	}


}
