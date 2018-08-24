package network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.RecentPasser;

/**
 * Sends {@link java.net.DatagramPacket DatagramPackets} through a {@link java.net.DatagramSocket DatagramSocket}. A <code>UDPSender</code> should
 * be run on its own {@link java.lang.Thread Thread}; its <code>run()</code> method blocks.
 *
 * @author Joe Desmond
 */
class UDPSender implements Runnable {
	private final DatagramSocket socket;
	private final InetAddress address;
	private final int clientPort;
	
	private final RecentPasser<String> sender;
	
	/**
	 * Creates a <code>UDPSender</code> that sends {@link java.lang.String Strings} received through <code>_sender</code> to the specified
	 * address and port, through <code>_socket</code>.
	 * 
	 * @param _socket <code>DatagramSocket</code> to send packets through
	 * @param _address address of the client to receive packets
	 * @param _clientPort port through which the client will receive packets
	 * @param _sender {@link network.RecentPasser RecentPasser} through which packets to be sent will be received
	 */
	public UDPSender(final DatagramSocket _socket, final InetAddress _address, final int _clientPort, final RecentPasser<String> _sender) {
		socket = _socket;
		address = _address;
		clientPort = _clientPort;
		sender = _sender;
		
		System.out.println("UDP Sender created at port " + socket.getLocalPort() + ", communicating with " + address.getHostAddress() + ".");
	}
	
	/**
	 * <b>DO NOT EXPLICITLY CALL THIS METHOD. USE THIS UDP SENDER IN A THREAD.</b>
	 * <p>
	 * <b>THIS METHOD BLOCKS INDEFINITELY.</b>
	 */
	@Override
	public void run() {
		try {
			
			while (true) {
				
				if (sender.hasNew()) {
					
					byte[] data = sender.retrieve().getBytes();
					
					DatagramPacket packet = new DatagramPacket(data, data.length, address, clientPort);
					
					socket.send(packet);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
