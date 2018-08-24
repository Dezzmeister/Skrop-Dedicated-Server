package network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.RecentPasser;

/**
 * Receives {@link java.net.DatagramPacket DatagramPackets} through a {@link java.net.DatagramSocket DatagramSocket}. 
 * Retains information about the client through the first
 * packet received. This information is made available through <code>getInitialAddress()</code> and <code>getInitialPort()</code>
 * so that a {@link UDPSender} knows where to send packets.
 * <p>
 * Because UDP is a connectionless protocol, UDP Senders and Receivers are run on separate {@link java.lang.Thread Threads} - this <code>UDPReceiver</code> should be run on
 * its own thread.
 *
 * @author Joe Desmond
 */
class UDPReceiver implements Runnable {
	private final DatagramSocket socket;
	
	private InetAddress initialAddress = null;
	private int initialPort = -1;
	
	private final RecentPasser<String> receiver;
	
	/**
	 * Creates a <code>UDPReceiver</code> that handles data received through <code>_socket</code>
	 * and passes it through <code>_receiver</code>.
	 * 
	 * @param _socket <code>DatagramSocket</code> to receive packets
	 * @param _receiver <code>RecentPasser</code> to receive data from <code>_socket</code>
	 */
	public UDPReceiver(DatagramSocket _socket, RecentPasser<String> _receiver) {
		socket = _socket;
		receiver = _receiver;
	}
	
	/**
	 * <b>DO NOT EXPLICITLY CALL THIS METHOD. USE THIS UDP RECEIVER IN A THREAD.</b>
	 * <p>
	 * <b>THIS METHOD BLOCKS INDEFINITELY.</b>
	 */
	@Override
	public void run() {
		try {
			byte[] buffer = new byte[65536];
			var incoming = new DatagramPacket(buffer, buffer.length);
			
			System.out.println("UDP Receiver listening at UDP port " + socket.getLocalPort() + ". Waiting for data...");
			
			while (true) {
				socket.receive(incoming);
				
				if (initialAddress == null) {
					initialAddress = incoming.getAddress();
					initialPort = incoming.getPort();
					System.out.println("UDP Server sending packets to " + initialAddress + " on port " + initialPort + ".");
				}
				
				byte[] data = incoming.getData();
				String received = new String(data, 0, incoming.getLength());
				
				System.out.println("UDP RECEIVED: " + received);
				
				receiver.pass(received);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the IP address of the client that sent the first packet this <code>UDPReceiver</code>
	 * received since its creation, or since the last call to {@link UDPReceiver#resetConnection() resetConnection()}.
	 * 
	 * @return initial IP address as an <code>InetAddress</code>
	 */
	public InetAddress getInitialAddress() {
		return initialAddress;
	}
	
	/**
	 * Returns the port of the client that sent the first packet this <code>UDPReceiver</code>
	 * received since its creation, or since the last call to {@link UDPReceiver#resetConnection() resetConnection()}.
	 * 
	 * @return initial port
	 */
	public int getInitialPort() {
		return initialPort;
	}
	
	/**
	 * Resets this <code>UDPReceiver</code>'s initial address and port so that a new address and port may be obtained
	 * for a <code>UDPSender</code> to send packets.
	 * <p>
	 * <b>DOES NOT OPEN/CLOSE ANY UDP SOCKETS.</b>
	 */
	public void resetConnection() {
		initialAddress = null;
	}
}
