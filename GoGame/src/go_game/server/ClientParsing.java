package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import go_game.protocol.Constants3;
import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * Client class for a simple client-server application.
 * @author  Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class ClientParsing extends Thread implements Constants4 {

	// Main, When starting command line ----------------------------------------------------------------------------------
	/**
	 *  Start een Client-applicatie op.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println(USAGE);
			//				System.exit(0);
			return;
		}

		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			print("ERROR: no valid hostname!");
			//				System.exit(0);
			return;
		}

		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			print("ERROR: no valid portnummer!");
			//				System.exit(0);
			return;
		}

		try {
			System.out.println("Trying to connect to server");
			client = new ClientParsing(args[0], host, port);

			// --------------------------------
			// SHUTDOWN HOOK FOR CTRL + C
			// --------------------------------
			// Add shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					// Nu kan ik bij de client
					System.out.println("Client not nicely closed...");
					// Calling shutdown function
					ClientParsing.client.shutdown();
					System.out.println(".. but still shutdown is called!");
					return;
				}
			});

			// --------------------------------
			// STARTING READING THREAD
			// --------------------------------
			client.sendMessage(NEWPLAYER + DELIMITER + args[0]);
			// Running seperate thread for incoming messages
			client.start();

			// --------------------------------
			// WRITING THREAD
			// --------------------------------
			while (!terminated) {
				String input = readString("");
				client.sendMessage(input);
			} 

			System.out.println("Kom ik aan het einde van de main functie??");
		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");

			return;
		}

	}
	
	/**
	 *  Read strings, used in the static main function.
	 *
	 * @param tekst the tekst
	 * @return the string
	 */
	public static String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			// Blocking call
			antw = in.readLine();
		} catch (IOException e) {
			System.out.println("Breaked out of reading loop.");
		}
		return (antw == null) ? "" : antw;
	}
	
	/**
	 *  send a message to a ClientHandler.
	 *
	 * @param msg the msg
	 */
	public void sendMessage(String msg) {
		//			System.out.println(getClientName() + " sent: " + msg);
		try {
			String parsedMessage = mClientIOParser.parseOutput(msg); 
			out.write(parsedMessage + "\n");
			out.flush();
		} catch (IOException e) { 
			System.out.println("Break from sendMessage");
//			e.printStackTrace(); 
		}
	}
	
	/**
	 *  Additional writing, instead of System.out.println.
	 *
	 * @param message the message
	 */
	public static void print(String message) {
		System.out.println(message);
	}

	/** The client name. */
	// Instance variables -------------------------------------------------------------
	private String clientName;
	
	/** The sock. */
	private Socket sock;
	
	/** The in. */
	private BufferedReader in;
	
	/** The out. */
	private BufferedWriter out;
	
	/** The m client io parser. */
	private ClientIOParser mClientIOParser;
	
	/** The terminated. */
	private static boolean terminated;
	
	/** The Constant USAGE. */
	private static final String USAGE = "usage: java go_game.server.ClientParsing <name> <address> <port>";
	
	/** The client. */
	public static ClientParsing client;

	// Constructor --------------------------------------------------------------------
	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 *
	 * @param name the name
	 * @param host the host
	 * @param port the port
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientParsing(String name, InetAddress host, int port)
			throws IOException {
		// Set the name and open the socket
		this.clientName = name;
		this.sock = new Socket(host, port);
		this.setName("ClientParsing thread");
		// Create the writer and reader streams
		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		System.out.println("Connected to server, just start talking!");
		
		terminated = false;
		
		mClientIOParser = new ClientIOParser(this);
	}

	// --------------------------------
	// RUN FUNCTION OF READING THREAD
	// --------------------------------
	/**
	 * Reads the messages in the socket connection. Each message will
	 * be forwarded to the MessageUI
	 */
	public void run() {
		char[] inputChars = new char[1024];
		int charsRead = 0;
		try {
			clientloop:
			while (!terminated) {
				if ((charsRead = in.read(inputChars)) != -1) {
					String temp = new String(inputChars).substring(0, charsRead);
					String inputString = mClientIOParser.parseInput(temp);
					System.out.println(inputString);
					System.out.flush();
				}
			} 
		} catch (IOException e) {
			System.out.println("Break from reading run-loop");
//			e.printStackTrace();
		}
	}


	// --------------------------------
	// STARTING READING THREAD
	// --------------------------------


	/** Close the socket connection. */
	public void shutdown() {
		System.out.println("Closing socket connection...");
		try {
			out.write(QUIT + "\n");
			out.flush();
			terminated = true;
			sock.close();
			in.close();
			out.close();
			System.out.println("System succesfully shutdown!\n");
			this.interrupt();
			
		} catch (IOException e) {
			System.out.println("Going back to the command line... \n");
//			e.printStackTrace();
			
		}
	}

	// --------------------------------
	// GETTERS AND SETTERS
	// --------------------------------
	/**
	 *  returns the client name.
	 *
	 * @return the client name
	 */
	public String getClientName() {
		return clientName;
	}
	
	/**
	 *  returns the client name.
	 *
	 * @param newName the new client name
	 */
	public void setClientName(String newName) {
		this.clientName = newName;
	}
}


