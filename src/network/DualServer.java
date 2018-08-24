package network;

import network.udp.UDPServer;

public final class DualServer {
	private final Communicator<String> tcpCommunicator = new Communicator<String>();
	private final Communicator<String> udpCommunicator = new Communicator<String>();
	
	private final int tcpPort;
	private final int udpPort;
	
	private final TCPServer tcpServer;
	private final UDPServer udpServer;
	
	private final Thread tcpThread;
	private final Thread udpThread;
	
	public DualServer(int _tcpPort, int _udpPort) {
		tcpPort = _tcpPort;
		udpPort = _udpPort;
		
		tcpServer = new TCPServer(tcpPort, tcpCommunicator);
		udpServer = new UDPServer(udpPort, udpCommunicator);
		
		tcpThread = new Thread(tcpServer, "Skrop TCP Main Server");
		udpThread = new Thread(udpServer, "Skrop UDP Main Server");
	}
	
	/**
	 * Starts the TCP and UDP servers.
	 */
	public void start() {
		tcpThread.start();
		udpThread.start();
	}
	
	public void sendTCP(String message) {
		tcpCommunicator.send(message);
	}
	
	public void sendUDP(String message) {
		udpCommunicator.send(message);
	}
	
	/**
	 * Returns the latest, new String received by the TCP server. If the latest message is not new,
	 * returns null.
	 * 
	 * @return latest message received from TCP Server, unless message has been checked before
	 */
	public String receiveTCP() {
		if (tcpCommunicator.hasNew()) {
			return tcpCommunicator.receive();
		}
		
		return null;
	}
	
	/**
	 * Returns the latest, new String received by the UDP server. If the latest message is not new,
	 * returns null.
	 * 
	 * @return latest message received from UDP Server, unless message has been checked before
	 */
	public String receiveUDP() {
		if (udpCommunicator.hasNew()) {
			return udpCommunicator.receive();
		}
		
		return null;
	}
}
