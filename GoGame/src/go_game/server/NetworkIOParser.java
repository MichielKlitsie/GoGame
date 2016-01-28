package go_game.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.protocol.Constants2;
import go_game.protocol.Constants3;
import go_game.protocol.Constants4;

public class NetworkIOParser implements Constants4 {

	private static final String GETALLTHREADS = "GETALLTHREADS";
	// Instance variables ----------------------------
	private String inputString;
	private String outputString;
	private String nameChallenger; 
	private String nameChallenged; 
	private ClientHandler clientHandler;
	private Server server;
	private static final String BOARDSIZE = "9";
	private Logger logger;

	// STATES OF THE CORRESPONDING CLIENTHANDLER
	private boolean isPlaying;
	private boolean isInLobby;
	private boolean isPendingChallenge;
	private boolean isWaitingOnTurn;
	private boolean isAlreadyChallenged;
	private boolean isObserving;
	private boolean isWaitingOnRandomPlay;
	private boolean isInWaitingRoom;

	// PREMADE STRINGS
	private String pendingChallengeMessage = "You are waiting on a challenge response, do not try to do anything crazy.\n";



	// ADDITIONAL COMMANDS
	private static final String GETSTATUS = "GETSTATUS";
	private static final String CLIENTEXIT = "CLIENTEXIT";
	private static final String EXIT = "EXIT";
	private static final String CHANGENAME = "CHANGENAME";
	private static final String STATUS = "STATUS";

	// Linebreak
	private static final String LINEBREAK = "//----------------------------\n";

	// Constructor ----------------------------
	/**
	 * Constructor.
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
	 * @param inputString
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
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + UNKNOWNCOMMAND);
			}
			break;

			//		case NEWPLAYERACCEPTED:
			//			System.out.println("Player Accepted");
			//			break;

		case GETOPTIONS: 
			// TODO REARRANGE ACCORDING TO OPTIONS
			if (this.isPendingChallenge) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsPendingChallenge() + LINEBREAK);
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + CANCEL + QUIT + DELIMITER + GETOPTIONS);
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsLobby() + LINEBREAK );
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS);
			} else if (this.isPlaying) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsGame() + LINEBREAK );
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS);
			} else if (this.isWaitingOnTurn) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + clientHandler.getOptionsWaitingOnMove() + LINEBREAK );
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS);
			} else if (this.isObserving) {
				clientHandler.sendMessageToClient(CHAT+ DELIMITER + LINEBREAK + clientHandler.getOptionsObserving() + LINEBREAK );
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS);
			} else if (this.isObserving) {
				clientHandler.sendMessageToClient(CHAT+ DELIMITER + LINEBREAK + clientHandler.getOptionsIsInWaitingRoom() + LINEBREAK );
				clientHandler.sendMessageToClient(OPTIONS + DELIMITER + QUIT + DELIMITER + GETOPTIONS);
			} else {
				clientHandler.sendMessageToClient(FAILURE + " Your current state is unclear... Sorry!");
			}

			break;
			//
			//		case OPTIONS:
			//			//TODO: PROTOCOL OPTIONS
			//			break;

		case PLAY:
			if (this.isPendingChallenge || this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				List<ClientHandler> listOfPlayersWaiting = getListOfPlayersWaitingForRandomPlay();

				if (listOfPlayersWaiting.size() == 0) {
					clientHandler.sendMessageToClient(WAITFOROPPONENT);
					clientHandler.setWaitingForRandomPlay(true);
					clientHandler.setIsInLobby(false);
				} else {
					ClientHandler clientHandlerOpponent = listOfPlayersWaiting.get(0);
					String nameChallenged = clientHandlerOpponent.getClientName();
					String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + WHITE; 
					clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
					clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);
				}				
				break;
			}

			// TODO: ONLY FOR PARSING CLIENT SIDE?
		case WAITFOROPPONENT:
			if (this.isPendingChallenge || this.isInWaitingRoom || this.isWaitingOnRandomPlay || this.isPlaying || this.isObserving ) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "Wait for an opponent to play as well. \n");
				clientHandler.setWaitingForRandomPlay(true);
				break;
			}

		case GAMESTART: 
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				if (amountArgs == 3) {
					// Parse arguments
					String namePlayer = stringParts[1];
					int boardDimension = Integer.parseInt(stringParts[2]);
					String mark = stringParts[3];
					logger.log(Level.INFO, "\nGame started by challenge player " + namePlayer + 
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
						ClientHandler temp = searchClientHandlerByName(nameChallenger);
						clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, temp);
						//						outerloop:
						//							for (ClientHandler temp : availablePlayers) {
						//								// temp is the challenger
						//								if(temp.getClientName().trim().equals(nameChallenger)) {
						//									clientHandler.sendGameStartToServer(nameChallenger, nameChallenged, 9, BLACK, temp);
						//									break outerloop;
						//								}
						//							}
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
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
			} else {
				if (isWaitingOnTurn) {
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTYOURTURN);
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
							clientHandler.sendMessageToClient(FAILURE + DELIMITER + INVALIDMOVE);

						} else if (singleInputArg.matches(patternOneNumber)) {
							System.out.println("Parsing the one argument move command");
							// INPUT INDEX
							choiceIndex = Integer.parseInt(singleInputArg) - 1;
							//						isValidInput = true;
							clientHandler.sentParsedMoveToGoGameServer(choiceIndex);

						} else {
							// OR ELSE....
							clientHandler.sendMessageToClient(FAILURE + DELIMITER + INVALIDMOVE);
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
							clientHandler.sendMessageToClient(FAILURE + DELIMITER + INVALIDMOVE);
						}
						// If the user inputted a pass, continue with value -1	
					} else if (amountArgs >= 3) {
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
					} else {
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
					}
				}
			}
			break;

		case PASS:
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
			} else {
				// Send the MOVE COMMAND INCLUDING PASS
				clientHandler.sendMessageToServer(MOVE + DELIMITER + PASS);
			}
			break;
		case GETHINT:
			//TODO GET THE HINT COMMAND
			if(isPlaying) {
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "Hint by server: \n");
				clientHandler.sendMessageToClient(HINT + DELIMITER + "5, 5");
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
			}
			break;
			// TODO: BOARD SENDING....	
		case GETBOARD: 
			if (this.isPendingChallenge || this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				// Obtain the string representation of the board
				String stringBoard = clientHandler.getProtocolStringRepresentationBoard();
				System.out.println("Board status sent: " + stringBoard);
				clientHandler.sendMessageToClient(BOARD + DELIMITER + stringBoard);
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
				clientHandler.setIsInLobby(true);
				clientHandler.setIsWaitingOnTurn(false);
				// TODO: AND LET THE SERVER KNOW THE PLAYING STATUS IS RETRACTED
			} else if (this.isObserving) {
				clientHandler.setIsObserving(false);
				clientHandler.setObserverModeOff();
				clientHandler.sendMessageToClient("You stopped observing the game\n");
				clientHandler.setIsInLobby(true);
			} else if (this.isPlaying) {
				// Step 1: PERSON SENDING QUIT
				clientHandler.sendMessageToClient("Quiting game...");
				// Step 2: SENDING QUIT TO OPPONENT, is that opponent is still playing 
				if (clientHandler.getClientHandlerOpponent().getIsPlaying()) {
					clientHandler.getClientHandlerOpponent().sendMessageToClient("Opponent has quit the game");
					clientHandler.getClientHandlerOpponent().sendMessageToServer(CANCEL);	
				}
				clientHandler.setIsPlaying(false);
				clientHandler.setIsInLobby(true);
			} else if (this.isObserving) {
				clientHandler.setIsObserving(false);
				clientHandler.setObserverModeOff();
				clientHandler.sendMessageToClient(CHAT + DELIMITER + "You stopped observing the game\n");
				clientHandler.setIsInLobby(true);
			} else if (this.isInLobby) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
			}
			break;
		case QUIT: 
			// DISCONNECTING FROM SERVER
			//			 if (this.isInLobby) {
//			server.removeHandler(clientHandler);
//			clientHandler.getServer().removeHandler();
//			clientHandler.sendMessageToClient("[" + clientHandler.getClientName() + "has left the server]");
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
			clientHandler.sendMessageToClient(GAMEOVER);
			clientHandler.sendMessageToClient(CHAT + DELIMITER + "Welcome back in the lobby!");
			server.broadcast(CHAT + DELIMITER + "[" + clientHandler.getClientName() + " is back in the lobby]\n");
			break;
			// Arguments
			//			case BOARDSIZE : d = 1; break;
			//			case COLOR: d = 1; break;
			//		case VICTORY: d = 1; break;
			//		case DEFEAT: d = 1; break;

			// Options ---------------------------------------------------------------
		case CHAT:
			String chatMessage = CHAT + DELIMITER + clientHandler.getClientName() + ": " + 
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
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break; 
			} else {
				// SHOW A LIST OF AVAILABLE CHALLENGERS
				if (amountArgs == 0) {
					// SEND BACK

					// GET a string OF AVAILABLE PLAYERS
					List<ClientHandler> listPlayersInLobby = getListOfPlayersInLobby();
					String s = LINEBREAK + "Players in the lobby: \n";
					s = s + createStringOfListPlayers(listPlayersInLobby) + LINEBREAK;
					clientHandler.sendMessageToClient(CHAT + DELIMITER + s);
					clientHandler.sendMessageToClient(AVAILABLEPLAYERS + DELIMITER + createSimpleStringOfListPlayers(listPlayersInLobby));

					// CHALLENGE A SPECIFIC PLAYER
				} else if (amountArgs == 1) {
					String nameToBeChallenged = stringParts[1].trim();
					String nameChallenger = clientHandler.getClientName().trim();
					boolean challengerAvailable = false;

					// Check for own name. You cannot challenge yourself...
					if (nameToBeChallenged.equals(nameChallenger)) {
						//						String challengeSelfMessage = "You cannot challenge yourself, try again.";
						//						clientHandler.sendMessageToClient(challengeSelfMessage);
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + PLAYERNOTAVAILABLE);
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
									clientHandler.sendMessageToClient(YOUVECHALLENGED + DELIMITER + nameToBeChallenged);
									clientHandler.setPendingChallengeStatus(true);
									// Disabling the lobby functionallity
									clientHandler.setIsInLobby(false);

									// TO BE CHALLENGED SIDE
									temp.sendMessageToClient(YOURECHALLENGED + DELIMITER + nameChallenger);
									//									temp.sendMessageToClient("You are challenged by '" + nameChallenger + "', respond with '" + CHALLENGEACCEPTED + "' or '" + CHALLENGEDENIED + "'.");
									temp.setIsAlreadyChallenged(true);
									challengerAvailable = true;
									clientHandler.setPendingChallengeStatus(true);

									// ... and set the duo on the list on the server
									server.addChallengePartners(nameChallenger, nameToBeChallenged);
								} else {
									clientHandler.sendMessageToClient("PLayer is already challenged by somebody else...");
								}
								break outerloop;
							} 
						}

					if (!challengerAvailable) {
						clientHandler.sendMessageToClient(FAILURE + DELIMITER + PLAYERNOTAVAILABLE);
					}



				} else {
					// More than 1 argument
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
				}; 
				break;
			}



		case AVAILABLEPLAYERS:
			if (this.isPendingChallenge || this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				List<ClientHandler> listPlayersInLobby = getListOfPlayersInLobby();
				String sNice = "Players in the lobby: \n";
				sNice = sNice + createStringOfListPlayers(listPlayersInLobby);
				clientHandler.sendMessageToClient(AVAILABLEPLAYERS + DELIMITER + createSimpleStringOfListPlayers(listPlayersInLobby));
				clientHandler.sendMessageToClient(CHAT + DELIMITER + sNice);
				break;
			}

		case CHALLENGEACCEPTED: 
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
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
								logger.log(Level.INFO, "Starting match with " + temp.getClientName());

								//Send message to the challenger
								temp.sendMessageToClient(CHALLENGEACCEPTED);
								//								temp.sendMessageToClient("\nThe challenge has been accepted, get ready for the match!");

								// BROADCAST TO PEOPLE IN LOBBY TWO PLAYERS ARE LEAVING LOBBY TO PLAY A GAME
								server.broadcast(CHAT + DELIMITER + "[" + nameChallenger + " and " + nameChallenged + " are leaving the lobby to play a game]\n");
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
								temp.setPendingChallengeStatus(false);

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
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
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

		case OBSERVE: 
			if (isInLobby && !isAlreadyChallenged && !isPendingChallenge) {
				if (amountArgs == 0) {
					clientHandler.sendMessageToServer(CURRENTGAMES);
				} else if (amountArgs == 1) {
					String namePlayer = stringParts[1].trim();
					// TODO: GET THE OBSERVER STATUS RIGHT
					// Get the clientHandler of the namePlayer (if it exists)
					if (searchClientHandlerByNameExists(namePlayer)) {
						// And check if the player is currently playing a game
						ClientHandler observedPlayer = searchClientHandlerByName(namePlayer);
						if (observedPlayer.getIsPlaying()) {
							String messageObserveGame = OBSERVEDGAME + DELIMITER + WHITE + DELIMITER + "blaWhite" + DELIMITER + BLACK + DELIMITER + "blaBlack " + DELIMITER + "Board size: "+ BOARDSIZE + DELIMITER + "Board: \n";
							clientHandler.sendMessageToClient(messageObserveGame);
							//								clientHandler.sendMessageToClient("Observering game of " + namePlayer);
							clientHandler.setIsObserving(true);
							clientHandler.setIsInLobby(false);
							clientHandler.setObserverModeOn(observedPlayer);
						} else {
							clientHandler.sendMessageToClient(namePlayer + " is not playing a game");
						}
					} else {
						clientHandler.sendMessageToClient("The player does not exist");
					}

				} else if (amountArgs > 1) {
					clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
				}; 
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
			}
			break; 
			//		case NOGAMESPLAYING: 
			//			
			//			clientHandler.sendMessageToClient(strNobodyPlaying);
			//			break; 
		case CURRENTGAMES: 
			if (this.isPlaying) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				List<ClientHandler> listOfPlayersPlaying = getListOfPlayersPlaying();
				String strListPlaying = "";
				if (listOfPlayersPlaying.size() == 0) {
					clientHandler.sendMessageToClient(NOGAMESPLAYING);
				} else {
					strListPlaying = createStringOfListPlayers(listOfPlayersPlaying);
					clientHandler.sendMessageToClient(CURRENTGAMES + DELIMITER + strListPlaying);
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
			if (this.isPlaying || this.isPendingChallenge) {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + NOTAPPLICABLECOMMAND);
				break;
			} else {
				//			outputCommand = GAMESTART;
				nameChallenged = COMPUTER; 
				String gameArgs = nameChallenged + DELIMITER + BOARDSIZE + DELIMITER + BLACK;
				//			clientHandler.sendMessageToClient(GAMESTART + DELIMITER + gameArgs);
				clientHandler.sendMessageToServer(GAMESTART + DELIMITER + gameArgs);
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
			} else if (errorType.equalsIgnoreCase(ILLEGALARGUMENT)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(OTHERPLAYERCANNOTCHAT)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			} else if (errorType.equalsIgnoreCase(PLAYERNOTAVAILABLE)) {
				clientHandler.sendMessageToClient("The player you inputted is (currently) not available."); 
			} else if (errorType.equalsIgnoreCase(GAMENOTPLAYING)) {
				clientHandler.sendMessageToClient("This command is ... "); 
			}

			break; 

			// ---------------------------------------------------------------
			// EXTENSIONS
			// ---------------------------------------------------------------
			// ADDITIONAL COMMANDS
		case GETEXTENSIONS:
			//TODO OPTIONELE COMMANDOS
			clientHandler.sendMessageToClient(CHAT + DELIMITER + LINEBREAK + "Additional commands are: \n 1. GETSTATUS \n 2. TBA \n" + LINEBREAK);
			clientHandler.sendMessageToClient(EXTENSIONS + DELIMITER + GETSTATUS);
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
			clientHandler.sendMessageToClient(STATUS + DELIMITER + statusString);
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
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
			} else if (amountArgs == 1) {
				String newName = stringParts[1].trim();
				clientHandler.setClientName(newName);
				clientHandler.sendMessageToClient("Your name has been changed to " + newName);
			} else {
				clientHandler.sendMessageToClient(FAILURE + DELIMITER + ARGUMENTSMISSING);
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
			clientHandler.sendMessageToClient(FAILURE + DELIMITER + UNKNOWNCOMMAND);	
			break;
		}
		return outputCommand;

	}

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
	// ---------------------------------------------------------------
	private List<ClientHandler> getListOfPlayersInLobby() {
		List<ClientHandler> availablePlayers = this.server.getPlayersInLobby();
		return availablePlayers;
	}

	private List<ClientHandler> getListOfPlayersPlaying() {
		List<ClientHandler> playingPlayers = this.server.getPlayersPlaying();
		return playingPlayers;
	}

	private List<ClientHandler> getListOfPlayersWaitingForRandomPlay() {
		List<ClientHandler> availablePlayers = this.server.getPlayersWaitingForRandomPlay();
		return availablePlayers;
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
		s = s + "\n";
		return s;
	}

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
