package network.udp;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import network.Communicator;
import network.RecentPasser;

/**
 * Creates a UDP server. The server is always listening on a specified port. When a packet is received, the origin of the packet is saved
 * so that data can be send to the other device. This simulates a connection despite the lack of a formal connection in UDP.
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
	
	private DatagramChannel channel;
	
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
		ByteBuffer inBuffer = ByteBuffer.allocate(4096);
		inBuffer.clear();
		SocketAddress clientAddress = null;
		
		try {			
			channel.configureBlocking(false);
			
			while (true) {				
				SocketAddress address = channel.receive(inBuffer);
				
				if (address != null) {
					if (address != clientAddress) {
						System.out.println("UDP client connected!");
					}
					clientAddress = address;
					byte[] bytes = inBuffer.array();
					String inMessage = new String(bytes);
					System.out.println("UDP RECEIVED: " + inMessage);
					messageReceiver.pass(inMessage);
					inBuffer.clear();
				}
				
				if (messageSender.hasNew() && clientAddress != null) {
					String outMessage = messageSender.retrieve();
					
					ByteBuffer outBuffer = ByteBuffer.allocate(outMessage.length());
					outBuffer.clear();
					outBuffer.put(outMessage.getBytes());
					outBuffer.flip();
					
					channel.send(outBuffer, clientAddress);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createSocket() {
		try {
			channel = DatagramChannel.open();
			channel.socket().bind(new InetSocketAddress(port));
			
			System.out.println("UDP Datagram Socket created at UDP port " + port + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
