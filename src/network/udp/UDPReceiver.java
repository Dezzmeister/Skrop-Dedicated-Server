package network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import network.RecentPasser;

public class UDPReceiver implements Runnable {
	private final DatagramSocket socket;
	private final int port;
	
	private InetAddress initialAddress = null;
	private int initialPort = -1;
	
	private final RecentPasser<String> receiver;
	
	public UDPReceiver(DatagramSocket _socket, int _port, RecentPasser<String> _receiver) {
		socket = _socket;
		port = _port;
		receiver = _receiver;
	}
	
	@Override
	public void run() {
		try {
			byte[] buffer = new byte[65536];
			var incoming = new DatagramPacket(buffer, buffer.length);
			
			System.out.println("UDP Receiver listening at port " + port + ". Waiting for data...");
			
			while (true) {
				socket.receive(incoming);
				
				if (initialAddress == null) {
					initialAddress = incoming.getAddress();
					initialPort = incoming.getPort();
				}
				
				byte[] data = incoming.getData();
				String received = new String(data, 0, incoming.getLength());
				
				System.out.println("RECEIVED: " + received);
				
				receiver.pass(received);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress getInitialAddress() {
		return initialAddress;
	}
	
	public int getInitialPort() {
		return initialPort;
	}
}
