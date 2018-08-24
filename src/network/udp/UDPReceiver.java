package network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import network.RecentPasser;

public class UDPReceiver implements Runnable {
	private final DatagramSocket socket;
	private AtomicReference<InetAddress> address = new AtomicReference<InetAddress>(null);
	private final int port;
	private AtomicInteger clientPort = new AtomicInteger(-1);
	
	private AtomicReference<DatagramPacket> initialPacket = new AtomicReference<DatagramPacket>(null);
	
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
				
				if (address.get() == null && clientPort.get() == -1 && initialPacket.get() == null) {
					while (!address.compareAndSet(null, incoming.getAddress()));
					while (!clientPort.compareAndSet(-1, incoming.getPort()));
					while (!initialPacket.compareAndSet(null, incoming));
				}
				
				byte[] data = incoming.getData();
				String received = new String(data, 0, incoming.getLength());
				
				receiver.pass(received);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DatagramPacket getInitialPacket() {
		return initialPacket.get();
	}
}
