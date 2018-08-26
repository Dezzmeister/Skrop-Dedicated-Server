package network.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import network.Communicator;
import network.RecentPasser;

/**
 * Creates a UDP server, which is always listening on a specified port (but not until <code>run()</code> is called). When a packet is received, the origin of the packet is saved
 * so that data can be send to the other device. This simulates a connection despite the lack of a formal connection in UDP.
 * <p>
 * A <code>UDPServer</code> should be started on a different {@link java.lang.Thread Thread}.
 *
 * @author Joe Desmond
 */
public class UDPServer implements Runnable {
	private int incomingPacketMaxLength = 4096;
	
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
	
	/**
	 * Creates a <code>UDPServer</code> that will send and receive data through <code>_port</code>. Received data will be available
	 * in <code>communicator</code>, which can also be used to send data.
	 * 
	 * @param _port UDP port to listen on
	 * @param communicator <code>Communicator</code> to send and receive String data
	 */
	public UDPServer(int _port, Communicator<String> communicator) {
		port = _port;
		messageSender = communicator.getSender();
		messageReceiver = communicator.getReceiver();
		
		createSocket();
	}
	
	/**
	 * <b>DO NOT EXPLICITLY CALL THIS METHOD. USE THIS UDP SERVER IN A THREAD.</b>
	 * <p>
	 * <b>THIS METHOD BLOCKS INDEFINITELY.</b>
	 */
	@Override	
	public void run() {
		ByteBuffer inBuffer = ByteBuffer.allocate(incomingPacketMaxLength);
		inBuffer.clear();
		InetSocketAddress clientAddress = null;
		
		try {
			System.out.println("UDP Server listening at UDP port " + port + ". Waiting for data...");
			
			channel.configureBlocking(false);
			
			while (true) {				
				InetSocketAddress address = (InetSocketAddress) channel.receive(inBuffer);
				
				if (address != null) {
					if (address != clientAddress) {
						System.out.println("UDP data received from " + address.getAddress().getHostAddress() + " on client UDP port " + address.getPort() + ".");
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
	
	/**
	 * Sets the maximum length of packets received by this {@link UDPServer}. Any extra characters will
	 * be discarded.
	 * 
	 * @param maxLength maximum packet length
	 */
	public void setIncomingPacketMaxLength(int maxLength) {
		incomingPacketMaxLength = maxLength;
	}
}
