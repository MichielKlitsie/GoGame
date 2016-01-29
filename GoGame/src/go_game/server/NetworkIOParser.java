package go_game.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.Board;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.RandomStrategy;
import go_game.Strategy;
import go_game.protocol.AdditionalConstants;
import go_game.protocol.Constants2;
import go_game.protocol.Constants3;
import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * The Class NetworkIOParser.
 */
public class NetworkIOParser implements Constants4, AdditionalConstants {

	/** The input string. */
	// Instance variables ----------------------------
	private String inputString;
	
	/** The output string. */
	private String outputString;
	
	/** The name challenger. */
	private String nameChallenger; 
	
	/** The name challenged. */
	private String nameChallenged; 
	
	/** The client handler. */
	private ClientHandler clientHandler;
	
	/** The server. */
	private Server server;
	
	/** The Constant BOARDSIZE. */
	private static final String BOARDSIZE = "9";
	
	/** The logger. */
	private Logger logger;

	/** The is playing. */
	// STATES OF THE CORRESPONDING CLIENTHANDLER
	private boolean isPlaying;
	
	/** The is in lobby. */
	private boolean isInLobby;
	
	/** The is pending challenge. */
	private boolean isPendingChallenge;
	
	/** The is waiting on turn. */
	private boolean isWaitingOnTurn;
	
	/** The is already challenged. */
	private boolean isAlreadyChallenged;
	
	/** The is observing. */
	private boolean isObserving;
	
	/** The is waiting on random play. */
	private boolean isWaitingOnRandomPlay;
	
	/** The is in waiting room. */
	private boolean isInWaitingRoom;

	/** The pending challenge message. */
	// PREMADE STRINGS
	private String pendingChallengeMessage = "You are waiting on a challenge response, do not try to do anything crazy.\n";





	// Constructor ----------------------------
	/**
	 * Constructor.
	 *
	 * @param clientHandler the client handler
	 * @param server the server
	 */
	public NetworkIOParser(ClientHandler clientHandler, Server server) {
		this.inputString = "";
		this.outputString = "";
		this.clientHandler = clientHandler;
		this.server = server;
		logger = server.LOGGER;
	}

	// Methods and commands ----------------------------
	/**
	 * Parse the incoming input of the client handler and call the corresponding functionality on the server side.
	 * For all commands, see the interface Constant3.java
	 *
	 * @param inputString the input string
	 * @return outputString (optional)
	 */
	public String parseInput(String inputString) {
		int d = 0;
		String outputCommand = "Command not recognized";

		// Split commando from arguments
		String[] stringParts = inputString.split(DELIMITER);
		String command = stringParts[0].trim().toUpperCase();
		String displayString = inputString.substring(command.length());
		int amountArgs = stringParts.length - 1;
		logger.log(Level.INFO, "Parsing: '" + command + "' with " + amountArgs + " arguments.");

		// GET THE STATE OF THE CORRESPONDING CLIENTHANDLER
		this.isPlaying = clientHandler.getIsPlaying();
		this.isInLobby = clientHandler.getIsInLobby();
		this.isPendingChallenge = clientHandler.getPendingChallengeStatus();
		this.isWaitingOnTurn = clientHandler.getIsWaitingOnTurn();
		this.isAlreadyChallenged = clientHandler.getIsAlreadyChallenged();
		this.isObserving = clientHandler.getIsObserving();
		this.isWaitingOnRandomPlay = clientHandler.getIsWaitingForRandomPlay();
		this.isInWaitingRoom = clientHandler.getIsInWaitingRoom();

		// Perform switch case...
		switch (command) {

		// GENERAL commands ------------------------------------------
		case VERSION:
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Current protocol version: " + VERSION+ "\n");
			break;
		case "SERVER_PORT":
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Current server port: " + SERVER_PORT+ "\n");
			break;
		case "TIMEOUTSECONDS":
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Current you have " + TIMEOUTSECONDS + " seconds until time-out"+ "\n");
			break;


			// BASIC LOBBY commands ------------------------------------------
		case NEWPLAYER:
			// Create a player from the commands
			String name = "";
			if (amountArgs == 0) {
				name = "NoNamePlayer";
			} else if (amountArgs == 1) {
				//				clientHandler = server.checkDoubleName(clientHandler);
				clientHandler.setClientName(stringParts[1].trim());
				//				clientHandler.sendMessageToClient(NEWPLAYERACCEPTED);
				//				clientHandler.sendMessageToServer(GETOPTIONS);
				//				clientHandler.sendMessageToServer(NEWPLAYERACCEPTED);
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + UNKNOWNCOMMAND + "\n");
			}
			break;

			//		case NEWPLAYERACCEPTED:
			//			System.out.println("Player Accepted");
			//			break;

		case GETOPTIONS: 
			// TODO REARRANGE ACCORDING TO OPTIONS
			if (this.isPendingChallenge) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsPendingChallenge() + LINEBREAK + "\n");
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + CANCEL + QUIT + DELIMITER + GETOPTIONS + "\n");
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsLobby() + LINEBREAK  + "\n");
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS + "\n");
			} else if (this.isPlaying) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsGame() + LINEBREAK  + "\n");
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS + "\n");
			} else if (this.isWaitingOnTurn) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsWaitingOnMove() + LINEBREAK  + "\n");
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS + "\n");
			} else if (this.isObserving) {
				clientHandler.sendMessageToClient(CHAT+ DELIMITER + LINEBREAK + clientHandler.getOptionsObserving() + LINEBREAK  + "\n");
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS + "\n");
			} else if (this.isObserving) {
				clientHandler.sendMessageToClient(CHAT+ DELIMITER + LINEBREAK + clientHandler.getOptionsIsInWaitingRoom() + LINEBREAK  + "\n");
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS + "\n");
			} else {
				clientHandler.sendMessageToClient(FAILURE + " Your current state is unclear... Sorry!"+ "\n");
			}

			break;
			//
			//		case OPTIONS:
			//			//TODO: PROTOCOL OPTIONS
			//			break;

		case PLAY:
			if (this.isPendingChallenge || this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
				List<ClientHandler> listOfPlayersWaiting = getListOfPlayersWaitingForRandomPlay();

				if (listOfPlayersWaiting.size() == 0) {
					clientHandler.sendMessageToClient(WAITFOROPPONENT + "\n");
					clientHandler.setIsWaitingForRandomPlay(true);
					clientHandler.setIsInLobby(false);
					server.broadcast(CHAT + DELIMITER + "[" + clientHandler.getClientName() + " is waiting on somebody to write 'PLAY']\n");
				} else {
					// LAST ONE REACTING IS SEEN AS HE WHO IS CHALLENGED
					ClientHandler clientHandlerOpponent = listOfPlayersWaiting.get(0);				
					String nameChallenged = clientHandler.getClientName().trim();
					String nameChallenger = clientHandlerOpponent.getClientName().trim();
					// Set them as a duo
					// ... and set the duo on the list on the server
					server.addChallengePartners(clientHandler.getClientName(), clientHandlerOpponent.getClientName());
					
					// GAMESTART PERSON CHALLENGED
					String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + WHITE; 
					clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs + "\n");
//					clientHandler.setWaitingForRandomPlay(false);
					clientHandler.setClientHandlerOpponent(clientHandlerOpponent);

					// GAMESTART CHALLENGER
					String gameArgs2 = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
					clientHandlerOpponent.sendMessageToClient(GAMESTART + DELIMITER + gameArgs2);
//					clientHandlerOpponent.setPendingChallengeStatus(false);
//					clientHandlerOpponent.setWaitingForRandomPlay(false);
					
					// So the person already waiting can find
//					String gameArgs = clientHandlerOpponent.getClientName().trim() + DELIMITER + BOARDSIZE + DELIMITER + WHITE; 

					// GAMESTART TO SERVER FROM THE PERSON CHALLENGED
					clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);
				}				
				break;
			}

			// TODO: ONLY FOR PARSING CLIENT SIDE?
		case WAITFOROPPONENT:
			if (this.isPendingChallenge || this.isInWaitingRoom || this.isWaitingOnRandomPlay || this.isPlaying || this.isObserving ) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "Wait for an opponent to play as well. \n");
				clientHandler.setIsWaitingForRandomPlay(true);
				break;
			}

		case GAMESTART: 
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
				if (amountArgs == 3) {
					// Parse arguments
					
					nameChallenged = clientHandler.getClientName();
					int boardDimension = Integer.parseInt(stringParts[2]);
					String mark = stringParts[3];
					
					String namePlayer = stringParts[1];
					if (namePlayer.equals(COMPUTER)) {
						// Set a computer player
						// Instead of using the clientHandler from the other player, the last input is the players own clientHandler
						nameChallenger = clientHandler.getClientName().trim(); 
						nameChallenged = namePlayer;
						clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, clientHandler);
					} else {
						// RESPONSIBILITY ON THE CHALLENGED SIDE TO START THE GAME, THUS FIND THE CHALLENGER (AGAIN)
						ClientHandler clientHandlerChallenger = clientHandler.getClientHandlerOpponent();
						String nameChallenger = clientHandlerChallenger.getClientName();
						logger.log(Level.INFO, "\nGame started by challenge player " + namePlayer + " (" + mark +") against " + nameChallenger + 
								" on a board of size " + boardDimension + ".");
						
						clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, boardDimension, BLACK, clientHandlerChallenger);
					}
//				} else if (amountArgs == 4) {
//					//THIS IS THE CASE WHEN A COMPUTER STRATEGY IS INPUTTED
//					String namePlayer = stringParts[1];
//					nameChallenged = clientHandler.getClientName();
//					int boardDimension = Integer.parseInt(stringParts[2]);
//					String mark = stringParts[3];
//					String chosenStrategy = stringParts[4];
//					nameChallenger = clientHandler.getClientName().trim(); 
//					nameChallenged = namePlayer;
//					clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, clientHandler);
				} else {
					// Do Nothing
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
				}
				break;
			}


			// ------------------------------------------------------------------
			// BASIC GAME commands ------------------------------------------
			// ------------------------------------------------------------------
		case MOVE:
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
			} else {
				if (isWaitingOnTurn) {
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTYOURTURN + "\n");
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
						String singleInputArg = stringParts[1].trim();

						if (singleInputArg.equalsIgnoreCase("pass")) {
							System.out.println("Parsing the pass move command");
							//					choiceIndex = -1;
							clientHandler.sentParsedMoveToGoGameServer(PASS);
							//					clientHandler.sendMessageToClient(PASS);
							//					isValidInput = true;

						} else if (singleInputArg.matches(patternOneLetter)) {
							// INPUT SINGLE LETTER
							clientHandler.sendMessageToClient(FAILURE + DELIMITER + INVALIDMOVE + "\n");

						} else if (singleInputArg.matches(patternOneNumber)) {
							System.out.println("Parsing the one argument move command");
							// INPUT INDEX
							choiceIndex = Integer.parseInt(singleInputArg) - 1;
							//						isValidInput = true;
							clientHandler.sentParsedMoveToGoGameServer(choiceIndex);

						} else {
							// OR ELSE....
							clientHandler.sendMessageToClient(FAILURE + DELIMITER + INVALIDMOVE + "\n");
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
							clientHandler.sendMessageToClient(FAILURE + DELIMITER + INVALIDMOVE + "\n");
						}
						// If the user inputted a pass, continue with value -1	
					} else if (amountArgs >= 3) {
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
					} else {
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
					}
				}
			}
			break;

		case PASS:
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
			} else {
				// Send the MOVE COMMAND INCLUDING PASS
				clientHandler.sendMessageToServer(MOVE + DELIMITER + PASS + "\n");
			}
			break;
		case GETHINT:
			//TODO GET THE HINT COMMAND
			if(this.isPlaying) {
				Board currentBoard = clientHandler.getCurrentGameServer().getCurrentGame().getCurrentBoard();
				Strategy randomStrategy = new RandomStrategy(); 
				int index = randomStrategy.determineMove(currentBoard, clientHandler.getLastMark());
				clientHandler.sendMessageToClient(HINT + DELIMITER + currentBoard.getRow(index) + DELIMITER + currentBoard.getCol(index) + "\n");
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
			}
			break;
			// TODO: BOARD SENDING....	
		case GETBOARD: 
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
				// Obtain the string representation of the board
				String stringBoard = clientHandler.getProtocolStringRepresentationBoard();
				System.out.println("Board status sent: " + stringBoard);
				clientHandler.sendMessageToClient(BOARD + DELIMITER + stringBoard + "\n");
				break;
			}
			//		case BOARD: 
			//			//TODO: PROTOCOL COMMAND BOARD
			//			break;
		case CANCEL:
			if (this.isPendingChallenge) {
				// Retract the challenge and change the status
				clientHandler.setPendingChallengeStatus(false);
				clientHandler.setIsInLobby(true);
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "Challenge is retracted.\n");

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
							temp.sendMessageToClient(CHAT + DELIMITER + "The challenge has been retracted by " + nameChallenger + "\n");
							temp.setIsAlreadyChallenged(false);
							break outerloop;
						}
					}

				// And remove the challenge partners
				challengePartners.remove(nameChallenged);

				//				break;
			} else if (this.isWaitingOnRandomPlay) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "You are back in the lobby!" + "\n");
				clientHandler.setIsInLobby(true);
				clientHandler.setIsWaitingForRandomPlay(false);
				// TODO: AND LET THE SERVER KNOW THE PLAYING STATUS IS RETRACTED
			} else if (this.isObserving) {
				clientHandler.setIsObserving(false);
				clientHandler.setObserverModeOff();
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "You stopped observing the game"+ "\n");
				clientHandler.setIsInLobby(true);
			} else if (this.isPlaying) {
				// Step 1: PERSON SENDING QUIT
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "Quiting game..." + "\n");
				clientHandler.getCurrentGameServer().getCurrentGame().closeGame();
				// Step 2: SENDING QUIT TO OPPONENT, is that opponent is still playing 
//				if (clientHandler.getClientHandlerOpponent().getIsPlaying()) {
//					clientHandler.getClientHandlerOpponent().sendMessageToClient("Opponent has quit the game");
//					clientHandler.getClientHandlerOpponent().sendMessageToServer(CANCEL);	
//				}
				clientHandler.setIsPlaying(false);
				clientHandler.setIsInLobby(true);
			} else if (this.isObserving) {
				clientHandler.setIsObserving(false);
				clientHandler.setObserverModeOff();
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "You stopped observing the game"+ "\n");
				clientHandler.setIsInLobby(true);
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
			}
			break;
		case QUIT: 
			// DISCONNECTING FROM SERVER
			//			 if (this.isInLobby) {
//			server.removeHandler(clientHandler);
//			clientHandler.getServer().removeHandler();
//			clientHandler.sendMessageToClient(CHAT + DELIMITER + "[" + clientHandler.getClientName() + "has left the server]");
			clientHandler.shutdown();
//			clientHandler.interrupt();
			System.out.println("Networkparser parsed QUIT");
			break;
			//			} 

			// ------------------------------------------------------------------
			// END GAME COMMANDS	
			// ------------------------------------------------------------------
			//		case GAMEOVER: d = 1; break;
			// TODO: SEND VICTORY OR DEFEAT AS GAMEOVER + DELIMITER + DEFEAT
		case STOPGAME:
			// Sent the clients back to the lobby
			clientHandler.setIsInLobby(true);
			clientHandler.setIsPlaying(false);
			clientHandler.setIsObserving(false);
			clientHandler.setIsWaitingOnTurn(false);
			clientHandler.sendMessageToClient(GAMEOVER+ "\n");
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Welcome back in the lobby!" + "\n");
			server.broadcast(CHAT + DELIMITER + "[" + clientHandler.getClientName() + " is back in the lobby]" + "\n");
			break;
			// Arguments
			//			case BOARDSIZE : d = 1; break;
			//			case COLOR: d = 1; break;
			//		case VICTORY: d = 1; break;
			//		case DEFEAT: d = 1; break;

			// Options ---------------------------------------------------------------
		case CHAT:
			String chatMessage = CHAT + DELIMITER + clientHandler.getClientName() + ": " + 
					inputString.substring(command.length()) + "\n";

			// Get if the player is in the lobby or playing a game...
			if(this.isInLobby) {
				server.broadcastInLobby(chatMessage);
			} else if (this.isPlaying) {
				// GET THE OPPONENT
//				clientHandler.getClientHandlerOpponent().sendMessageToClient(chatMessage);
				clientHandler.getCurrentGameServer().sendMessageBoth(chatMessage);
				clientHandler.getCurrentGameServer().sendMessageToObservers(chatMessage);
			} else if (this.isObserving) {
				clientHandler.getObservedGameServer().sendMessageBoth(chatMessage);
				clientHandler.getObservedGameServer().sendMessageToObservers(chatMessage);
			}
			break;

			// ---------------------------------------------------------------
			// CHALLENGE ---------------------------------------------------------------
			// ---------------------------------------------------------------
		case CHALLENGE:
			if (this.isPlaying || this.isPendingChallenge) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break; 
			} else {
				// SHOW A LIST OF AVAILABLE CHALLENGERS
				if (amountArgs == 0) {
					// SEND BACK

					// GET a string OF AVAILABLE PLAYERS
					List<ClientHandler> listPlayersInLobby = getListOfPlayersInLobby();
					String s = LINEBREAK + "Players in the lobby: \n";
					s = s + createStringOfListPlayers(listPlayersInLobby) + LINEBREAK;
					clientHandler.sendMessageToClient(CHAT + DELIMITER + s + "\n");
					clientHandler.sendMessageToClient(AVAILABLEPLAYERS + DELIMITER + createSimpleStringOfListPlayers(listPlayersInLobby) + "\n");

					// CHALLENGE A SPECIFIC PLAYER
				} else if (amountArgs == 1) {
					String nameToBeChallenged = stringParts[1].trim();
					String nameChallenger = clientHandler.getClientName().trim();
					boolean challengerAvailable = false;

					// Check for own name. You cannot challenge yourself...
					if (nameToBeChallenged.equals(nameChallenger)) {
						//						String challengeSelfMessage = "You cannot challenge yourself, try again.";
						//						clientHandler.sendMessageToClient(challengeSelfMessage);
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + PLAYERNOTAVAILABLE + "\n");
						clientHandler.setPendingChallengeStatus(false);
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
									clientHandler.sendMessageToClient(YOUVECHALLENGED + DELIMITER + nameToBeChallenged + "\n");
									clientHandler.setPendingChallengeStatus(true);
									// Disabling the lobby functionallity
									clientHandler.setIsInLobby(false);

									// TO BE CHALLENGED SIDE
									temp.sendMessageToClient(YOURECHALLENGED + DELIMITER + nameChallenger + "\n");
									//									temp.sendMessageToClient("You are challenged by '" + nameChallenger + "', respond with '" + CHALLENGEACCEPTED + "' or '" + CHALLENGEDENIED + "'.");
									temp.setIsAlreadyChallenged(true);
									challengerAvailable = true;
									clientHandler.setPendingChallengeStatus(true);

									// ... and set the duo on the list on the server
									server.addChallengePartners(nameChallenger, nameToBeChallenged);
								} else {
									clientHandler.sendMessageToClient(CHAT + DELIMITER + "PLayer is already challenged by somebody else..." + "\n");
								}
								break outerloop;
							} 
						}

					if (!challengerAvailable) {
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + PLAYERNOTAVAILABLE + "\n");
					}



				} else {
					// More than 1 argument
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
				}; 
				break;
			}



		case AVAILABLEPLAYERS:
			if (this.isPendingChallenge || this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
				List<ClientHandler> listPlayersInLobby = getListOfPlayersInLobby();
				String sNice = "Players in the lobby: \n";
				sNice = sNice + createStringOfListPlayers(listPlayersInLobby);
				clientHandler.sendMessageToClient(AVAILABLEPLAYERS + DELIMITER + createSimpleStringOfListPlayers(listPlayersInLobby) + "\n");
				clientHandler.sendMessageToClient(CHAT + DELIMITER + sNice + "\n");
				break;
			}

		case CHALLENGEACCEPTED: 
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
//				if (this.isPendingChallenge) {
//					// This client must be the challenger and get the message from the server
//
//					// REMOVE self from lobby list
//					// GAMESTART
//					System.out.println("Oh jeahhhhh");
//					String nameChallenger = clientHandler.getClientName().trim(); 
//					String gameArgs = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
//					//					clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
//					//
//				} else {
					// THIS IS WHAT ACTUALLY HAPPENS 

					// This client must be the one challenged and in response sending the CHALLENGE ACCEPTED to the server
					//Get the pending challenger; 
					HashMap<String, String> challengePartners = server.getChallengePartners();
					nameChallenged = clientHandler.getClientName().trim();
					nameChallenger = challengePartners.get(nameChallenged);
					List<ClientHandler> availablePlayers = this.server.getAllPlayers();
					outerloop:
						for (ClientHandler clientHandlerOpponent : availablePlayers) {
							// temp is the challenger
							if(clientHandlerOpponent.getClientName().trim().equals(nameChallenger)) {
								logger.log(Level.INFO, "Starting match with " + clientHandlerOpponent.getClientName());

								//Send message to the challenger
								clientHandlerOpponent.sendMessageToClient(CHALLENGEACCEPTED + "\n");

								// BROADCAST TO PEOPLE IN LOBBY TWO PLAYERS ARE LEAVING LOBBY TO PLAY A GAME
								server.broadcast(CHAT + DELIMITER + "[" + nameChallenger + " and " + nameChallenged + " are leaving the lobby to play a game]" + "\n");

								// GAMESTART PERSON CHALLENGED
								String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + WHITE; 
								clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs + "\n");
								clientHandler.setClientHandlerOpponent(clientHandlerOpponent);

								// GAMESTART CHALLENGER
								String gameArgs2 = nameChallenger + DELIMITER + BOARDSIZE + DELIMITER + BLACK; 
								clientHandlerOpponent.sendMessageToClient(GAMESTART + DELIMITER + gameArgs2 + "\n");
								clientHandlerOpponent.setPendingChallengeStatus(false);

								// GAMESTART TO SERVER FROM THE PERSON CHALLENGED
								clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs + "\n");

								break outerloop;
							}
//						}
				}
				break; 
			}
		case CHALLENGEDENIED:
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
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
								clientHandler.sendMessageToClient(CHAT + DELIMITER +"Your challenge has been denied by " + nameChallenged + ", please try again!" + "\n");
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
								temp.sendMessageToClient(CHALLENGEDENIED + "\n");
								temp.sendMessageToServer(CHALLENGEDENIED + "\n");
								clientHandler.sendMessageToClient(CHAT + DELIMITER + "Your denial has been sent to " + nameChallenger + "\n");
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

		case OBSERVE: 
			if (isInLobby && !isAlreadyChallenged && !isPendingChallenge) {
				if (amountArgs == 0) {
					clientHandler.sendMessageToServer(CURRENTGAMES + "\n");
				} else if (amountArgs == 1) {
					String namePlayer = stringParts[1].trim();
					// TODO: GET THE OBSERVER STATUS RIGHT
					// Get the clientHandler of the namePlayer (if it exists)
					if (searchClientHandlerByNameExists(namePlayer)) {
						// And check if the player is currently playing a game
						ClientHandler observedPlayer = searchClientHandlerByName(namePlayer);
						if (observedPlayer.getIsPlaying()) {
							Player[] observedPlayers = observedPlayer.getCurrentGameServer().getCurrentGame().getPlayers();
							String messageObserveGame = OBSERVEDGAME + DELIMITER + WHITE + DELIMITER + observedPlayers[1].getName() + DELIMITER + BLACK + DELIMITER + observedPlayers[0].getName() + DELIMITER + "Board size: " + BOARDSIZE;
							clientHandler.sendMessageToClient(messageObserveGame + "\n");
							//								clientHandler.sendMessageToClient(CHAT + DELIMITER + "Observering game of " + namePlayer);
							clientHandler.setIsObserving(true);
							clientHandler.setIsInLobby(false);
							clientHandler.setObserverModeOn(observedPlayer);
						} else {
							clientHandler.sendMessageToClient(CHAT + DELIMITER + namePlayer + " is not playing a game" + "\n");
						}
					} else {
						clientHandler.sendMessageToClient(CHAT + DELIMITER + "The player does not exist" + "\n");
					}

				} else if (amountArgs > 1) {
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
				}; 
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
			}
			break; 
			//		case NOGAMESPLAYING: 
			//			
			//			clientHandler.sendMessageToClient(strNobodyPlaying);
			//			break; 
		case CURRENTGAMES: 
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
//				List<ClientHandler> listOfPlayersPlaying = getListOfPlayersPlaying();
//				String strListPlaying = "";
				Set<GoGameServer> games = server.getServerThreadObserver().getGameThreads();
				if (games.size() == 0) {
					clientHandler.sendMessageToClient(NOGAMESPLAYING + "\n");
				} else {
//					strListPlaying = createStringOfListPlayers(listOfPlayersPlaying);
					String s = "\n";
					int i = 1;
					for (GoGameServer game : games) {
						s = s + i + ". " + game.getName() + ": " + game.getCurrentGame().getPlayers()[0].getName() + " (Black) vs. " + game.getCurrentGame().getPlayers()[1].getName() + " (White) \n";
						i++;
					}
					clientHandler.sendMessageToClient(CURRENTGAMES + DELIMITER + s + "\n");
					
				}

				break;
			}
			//		case OBSERVEDGAME: d = 1; break; //OBSERVEDGAME";

			// ---------------------------------------------------------------
			// PRACTISE COMMANDS: AI
			// ---------------------------------------------------------------
		case COMPUTERPLAYER: d = 1; break; //COMPUTERPLAYER";
		//			case BOARDSIZES: d = 1; break; //BOARDSIZES";
		case PRACTICE: 
			
			// TODO: Prio2. Get the strategies right (parsing and shit)
			if (this.isPlaying || this.isPendingChallenge || this.isAlreadyChallenged || this.isObserving || this.isWaitingOnRandomPlay) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND + "\n");
				break;
			} else {
				if (amountArgs == 0) {
					clientHandler.sendMessageToClient(AVAILABLESTRATEGIES + DELIMITER + RANDOMSTRATEGY + DELIMITER + CUTTINGSTRATEGY + DELIMITER + MIRRORSTRATEGY + DELIMITER + SMARTSTRATEGY + "\n");
				} else if (amountArgs == 1) {
					String chosenStrategy = stringParts[1].trim();
					nameChallenged = COMPUTER; 
					clientHandler.setChosenStrategy(stringParts[1]);
					String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + BLACK + DELIMITER + chosenStrategy;
					//			clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
					clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs + "\n");
				} else {
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ILLEGALARGUMENT + "\n");
				}
				
				break; //PRACTICE";
			}
			//		case COMPUTER: d = 1; break; //COMPUTER";

			// --------------------------------------------------------------------
			// FAILURES AND Errors------------------------------------------------
			// --------------------------------------------------------------------
		case FAILURE: 
			String errorType = stringParts[1].trim();
			//			clientHandler.sendMessageToClient(errorType);
			//			clientHandler.sendMessageToServer(errorType);
			if (errorType.equalsIgnoreCase(NOTAPPLICABLECOMMAND)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is not applicable in your current state.\n");
			} else if (errorType.equalsIgnoreCase(UNKNOWNCOMMAND)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This is an unknown command. Please use a known command, see 'GETOPTIONS'.\n");
			} else if (errorType.equalsIgnoreCase(ARGUMENTSMISSING)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command comes with other arguments.\n");
			} else if (errorType.equalsIgnoreCase(NOTSUPPORTEDCOMMAND)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is not supported by the server.\n");
			} else if (errorType.equalsIgnoreCase(INVALIDNAME)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			} else if (errorType.equalsIgnoreCase(NAMETAKEN)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			} else if (errorType.equalsIgnoreCase(NAMENOTALLOWED)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			} else if (errorType.equalsIgnoreCase(INVALIDMOVE)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "No valid input, try again... \n"); 
			} else if (errorType.equalsIgnoreCase(NOTYOURTURN)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			} else if (errorType.equalsIgnoreCase(ILLEGALARGUMENT)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			} else if (errorType.equalsIgnoreCase(OTHERPLAYERCANNOTCHAT)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			} else if (errorType.equalsIgnoreCase(PLAYERNOTAVAILABLE)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "The player you inputted is (currently) not available."); 
			} else if (errorType.equalsIgnoreCase(GAMENOTPLAYING)) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "This command is ... "); 
			}

			break; 

			// ---------------------------------------------------------------
			// EXTENSIONS
			// ---------------------------------------------------------------
			// ADDITIONAL COMMANDS
		case GETEXTENSIONS:
			//TODO OPTIONELE COMMANDOS
			clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + "Additional commands are: \n 1. GETSTATUS \n 2. TBA \n" + LINEBREAK+ "\n");
			clientHandler.sendMessageToClient(EXTENSIONS + DELIMITER + GETSTATUS+ "\n");
			break;
		case EXTENSIONS:
			//TODO, lijst van optionele commando's sturen
			break;
		case GETSTATUS:
			String statusString = LINEBREAK + "Current status:" + 
					"\nIn the waiting room: " + isInWaitingRoom +
					"\nIn the lobby: " + isInLobby +
					"\nSent a challenge: " + isPendingChallenge +
					"\nRecieved a challenge: " + isAlreadyChallenged + 
					"\nPlaying a game: " + isPlaying +
					"\nWaiting for move opponent: " + isWaitingOnTurn + 
					"\nObserving: " + isObserving +
					"\nWaiting for a random play: " + isWaitingOnRandomPlay + "\n" + LINEBREAK ;
			clientHandler.sendMessageToClient(STATUS + DELIMITER + statusString+ "\n");
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
		case CHANGENAME:
			if (amountArgs == 0) {
				// No input name
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
			} else if (amountArgs == 1) {
				String newName = stringParts[1].trim();
				clientHandler.setClientName(newName);
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "Your name has been changed to " + newName);
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING + "\n");
			}
			break;
			// NOT RECOGNISED	
		case GETALLTHREADS:
			ServerThreadObserver observerThread = clientHandler.getServer().getServerThreadObserver();
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "ClientHandlers: " +observerThread.getClientHandlerThreads() + "\n");
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Gamethreads: " + observerThread.getGameThreads() + "\n");
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Serverthread: " + observerThread.getServerThread() + "\n");
			break;
		default: 
			//			System.out.println("Not parsing");
			//			outputCommand = UNKNOWNCOMMAND;
			//			System.out.println("Command sent to " + clientHandler.getClientName() + ": " + outputCommand);
			clientHandler.sendMessageToClient(FAILURE + DELIMITER + UNKNOWNCOMMAND + "\n");	
			break;
		}
		return outputCommand;

	}

	/**
	 * Search client handler by name.
	 *
	 * @param namePlayer the name player
	 * @return the client handler
	 */
	// Search for clientHandler by name
	private ClientHandler searchClientHandlerByName(String namePlayer) {
		List<ClientHandler> availablePlayers = this.server.getAllPlayers();
		ClientHandler playerTemp = null; // We can do this, because it is already checked that the player exists!
		nameloop:
			for (ClientHandler temp : availablePlayers) {
				// temp is the challenger
				if(temp.getClientName().trim().equals(namePlayer)) {
					playerTemp = temp;
					break nameloop;
				}
			}
		return playerTemp;
	}

	/**
	 * Search client handler by name exists.
	 *
	 * @param namePlayer the name player
	 * @return true, if successful
	 */
	private boolean searchClientHandlerByNameExists(String namePlayer) {
		boolean playerExists = false;
		List<ClientHandler> availablePlayers = this.server.getAllPlayers();
		namesearchloop:
			for (ClientHandler temp : availablePlayers) {
				// temp is the challenger
				if(temp.getClientName().trim().equals(namePlayer)) {
					playerExists = true;
					break namesearchloop;
				}
			}
		return playerExists;
	}

	// ---------------------------------------------------------------
	// GETTERS AND SETTERS
	/**
	 * Gets the list of players in lobby.
	 *
	 * @return the list of players in lobby
	 */
	// ---------------------------------------------------------------
	private List<ClientHandler> getListOfPlayersInLobby() {
		List<ClientHandler> availablePlayers = this.server.getPlayersInLobby();
		return availablePlayers;
	}

	/**
	 * Gets the list of players playing.
	 *
	 * @return the list of players playing
	 */
	private List<ClientHandler> getListOfPlayersPlaying() {
		List<ClientHandler> playingPlayers = this.server.getPlayersPlaying();
		return playingPlayers;
	}

	/**
	 * Gets the list of players waiting for random play.
	 *
	 * @return the list of players waiting for random play
	 */
	private List<ClientHandler> getListOfPlayersWaitingForRandomPlay() {
		List<ClientHandler> availablePlayers = this.server.getPlayersWaitingForRandomPlay();
		return availablePlayers;
	}

	/**
	 * Creates the string of list players.
	 *
	 * @param listClientHandlerPlayers the list client handler players
	 * @return the string
	 */
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
		s = s + "\n";
		return s;
	}

	/**
	 * Creates the simple string of list players.
	 *
	 * @param listClientHandlerPlayers the list client handler players
	 * @return the string
	 */
	private String createSimpleStringOfListPlayers(List<ClientHandler> listClientHandlerPlayers) {
		String s = "";
		for (int i = 0; i < listClientHandlerPlayers.size(); i++) {
			ClientHandler temp = listClientHandlerPlayers.get(i);
			// Discard own name
			if (temp.getClientName().trim() != this.clientHandler.getClientName()) {
				s = s + temp.getClientName().trim() + DELIMITER;
			}
		}
		return s;
	}


}
