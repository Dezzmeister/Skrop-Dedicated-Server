package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer implements Runnable {
	private DatagramSocket socket = null;
	private InetAddress clientAddress;
	private final int port;
	
	public UDPServer(int _port) {
		port = _port;
		
		try {
			socket = new DatagramSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			var buffer = new byte[65536];
			var incoming = new DatagramPacket(buffer, buffer.length);
			
			System.out.println("UDP socket created at port " + port + ". Waiting for data...");
			
			while (true) {
				socket.receive(incoming);
				byte[] data = incoming.getData();
				String s = new String(data, 0, incoming.getLength());
				
				System.out.println(s);
				s = "JOJ SOS: " + s;
				
				
				DatagramPacket reply = new DatagramPacket(s.getBytes(), s.getBytes().length, incoming.getAddress(), incoming.getPort());
				socket.send(reply);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
