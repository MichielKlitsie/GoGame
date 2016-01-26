package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import go_game.protocol.Constants2;


/**
 * Client class for a simple client-server application.
 * @author  Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class Client extends Thread implements Constants2 {
	private static final String USAGE = "usage: java week7.cmdchat.Client <name> <address> <port>";
	public static Client client;
//	// SHUTDOWN HOOK
//	public void attachShutDownHook(){
//		 
//		  });
//		  System.out.println("Shut Down Hook Attached.");
//		 }
	
	// Main ----------------------------------------------------------------------------------
	/** Start een Client-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println(USAGE);
//			System.exit(0);
			return;
		}

		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			print("ERROR: no valid hostname!");
//			System.exit(0);
			return;
		}

		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			print("ERROR: no valid portnummer!");
//			System.exit(0);
			return;
		}

		try {
			System.out.println("Trying to connect to server");
			client = new Client(args[0], host, port);
			
			// Add shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					// Nu kan ik bij de client
					System.out.println("Client not nicely closed...");
					Client.client.sendMessage("CLIENTEXIT");
//					Client.client.interrupt();
					Client.client.shutdown();
				}
			});
			
			// Handshake message is sending the name
			client.sendMessage(args[0]);
//			this.wait();
			client.start();

			do {
//				System.out.println("Enter something:");
				String input = readString("");
				client.sendMessage(input);
				
			} while (true);

		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
//			System.exit(0);
			return;
		}

	}

	// Instance variables -------------------------------------------------------------
	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	// Constructor --------------------------------------------------------------------
	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 */
	public Client(String name, InetAddress host, int port)
			throws IOException {
		// Set the name and open the socket
		this.clientName = name;
		this.sock = new Socket(host, port);

		// Create the writer and reader streams
		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
		System.out.println("Connected to server, just start talking!");
	}

	// Run ----------------------------------------------------------------------------
	/**
	 * Reads the messages in the socket connection. Each message will
	 * be forwarded to the MessageUI
	 */
	public void run() {
		char[] inputChars = new char[1024];
		int charsRead = 0;
		try {

			while (true) {
				if ((charsRead = in.read(inputChars)) != -1) {
					String temp = new String(inputChars).substring(0, charsRead);
					System.out.println(temp);
					System.out.flush();
					if (temp.equalsIgnoreCase("exit")) {
						System.out.println(": System shutting down");
						// TODO: SHUTDOWN: Write shutdown functionality in seperate observer class
						shutdown();
						return;
					}
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

	// Client functions----------------------------------------------------------------
	/** send a message to a ClientHandler. */
	public void sendMessage(String msg) {
//		System.out.println(getClientName() + " sent: " + msg);
		try {
			out.write(msg + "\n");
			out.flush();
			if (msg.equalsIgnoreCase("exit")) {
//				System.out.println("System shutting down..");
				shutdown();
			}
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		

		
	}


	/** close the socket connection. */
	public void shutdown() {
		System.out.println("Closing socket connection...");
		try {
			sock.close();
			in.close();
			out.close();
			System.out.println("System succesfully shutdown");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** returns the client name. */
	public String getClientName() {
		return clientName;
	}

	private static void print(String message) {
		System.out.println(message);
	}

	public static String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			// HIER BLIJFT IE OP HANGEN
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}
}
