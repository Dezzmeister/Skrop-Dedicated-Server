package network.udp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import network.RecentPasser;

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
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		UDPReceiver receiver = new UDPReceiver(socket, port, messageReceiver);
		
		receiverThread = new Thread(receiver);
		receiverThread.start();
		
		InetAddress initialAddress;
		int initialPort;
		
		while ((initialAddress = receiver.getInitialAddress()) == null);

		initialPort = receiver.getInitialPort();
		
		UDPSender sender = new UDPSender(socket, initialAddress, initialPort, messageSender);
		
		senderThread = new Thread(sender);
		senderThread.start();
		
		while (true);
	}
}
