package network.udp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import network.Communicator;
import network.RecentPasser;

/**
 * Creates a UDP server. The server begins by listening on a specified port until a datagram packet arrives, presumably from a client device.
 * The server obtains the IP and port of the sender, then passes this information to another sender {@link java.lang.Thread Thread}. Once this
 * <code>Thread</code> has the information, it can begin to send messages back to the client device.
 * <p>
 * A <code>UDPServer</code> should be started on a different <code>Thread</code>.
 *
 * @author Joe Desmond
 */
public class UDPServer implements Runnable {
	private DatagramSocket socket = null;
	public final int port;
	
	private final RecentPasser<String> messageReceiver;
	private final RecentPasser<String> messageSender;
	
	private Thread receiverThread;
	private Thread senderThread;
	
	public UDPServer(int _port, RecentPasser<String> _messageSender, RecentPasser<String> _messageReceiver) {
		port = _port;
		messageSender = _messageSender;
		messageReceiver = _messageReceiver;
		
		createSocket();
	}
	
	public UDPServer(int _port, Communicator<String> communicator) {
		port = _port;
		messageSender = communicator.sender;
		messageReceiver = communicator.receiver;
		
		createSocket();
	}
	
	/**
	 * <b>DO NOT EXPLICITLY CALL THIS METHOD. USE THIS UDP SERVER IN A THREAD.</b>
	 * <p>
	 * <b>THIS METHOD BLOCKS INDEFINITELY.</b>
	 */
	@Override
	public void run() {
		UDPReceiver receiver = new UDPReceiver(socket, messageReceiver);
		
		receiverThread = new Thread(receiver, "Skrop UDP Receiver Service");
		receiverThread.start();
		
		InetAddress initialAddress;
		int initialPort;
		
		while ((initialAddress = receiver.getInitialAddress()) == null);

		initialPort = receiver.getInitialPort();
		
		UDPSender sender = new UDPSender(socket, initialAddress, initialPort, messageSender);
		
		senderThread = new Thread(sender, "Skrop UDP Sender Service");
		senderThread.start();
		
		while (true);
	}
	
	private void createSocket() {
		try {
			socket = new DatagramSocket(port);
			System.out.println("UDP Datagram Socket created at UDP port " + port + ".");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
