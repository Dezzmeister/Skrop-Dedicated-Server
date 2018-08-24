package network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.RecentPasser;

public class UDPSender implements Runnable {
	private final DatagramSocket socket;
	private final InetAddress address;
	private final int clientPort;
	
	private final RecentPasser<String> sender;
	
	public UDPSender(DatagramSocket _socket, InetAddress _address, int _clientPort, RecentPasser<String> _sender) {
		socket = _socket;
		address = _address;
		clientPort = _clientPort;
		sender = _sender;
		
		System.out.println("UDP Sender created at port " + socket.getLocalPort());
	}
	
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
