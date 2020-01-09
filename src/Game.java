import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Orly
 *
 */
public class Game implements Runnable {

	private boolean isHost;
	private boolean isYourTurn;
	private boolean hasAccepted = false;
	
	private String ip;
	private int port;
	
	private Thread thread;
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ServerSocket serverSocket;
	private Scanner scanner = new Scanner(System.in);
	
	/**
	 * 
	 */
	public Game() {
		this.ip = "localhost";
		this.port = 12345;
		
		if (!attemptToConnect()) initialiseServer();
		
		thread = new Thread(this, "Game Thread");
		thread.start();
	}

	public static void main(String[] args) {
		
		Game game = new Game();
	}

	
	public void run() {
		while (true) {
			if (isHost && !hasAccepted) {
				listenForServerRequest();
			}
			if (isYourTurn) {
				System.out.println("Type an integer to send to the other user: ");
				try {
					dos.writeInt(scanner.nextInt());
				} catch (IOException e) {
					e.printStackTrace();
				}
				isYourTurn = false;
			} else {
				try {
					System.out.println("The other user has sent you the integer: " + dis.readInt());
				} catch (IOException e) {
					e.printStackTrace();
				}
				isYourTurn = true;
			}
		}
		
		
	}

	// attempts to connect to the given IP on the given Port
	// if a connection is unsuccessful, returns false
	private boolean attemptToConnect() {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server instead.");
			return false;
		}
		System.out.println("Successfully connected to the server.");
		isHost = false;
		isYourTurn = false;
		return true;
	}
	
	private void listenForServerRequest() {
		Socket socket = null;
		
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			hasAccepted = true;
			System.out.println("Another user has requested to join. Request accepted.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// hosts the server
	private void initialiseServer() {
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch (IOException e) {
			e.printStackTrace();
		}
		isHost = true;
		isYourTurn = true;
	}
}
