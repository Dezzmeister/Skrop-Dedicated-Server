package network;

import network.tcp.TCPServer;
import network.udp.UDPServer;

/**
 * Runs both a TCP and a UDP server. Creates two {@link java.lang.Thread Threads}, one for each server.
 *
 * @author Joe Desmond
 */
public final class DualServer {
	private final Communicator<String> tcpCommunicator = new Communicator<String>();
	private final Communicator<String> udpCommunicator = new Communicator<String>();
	
	private final int tcpPort;
	private final int udpPort;
	
	private final TCPServer tcpServer;
	private final UDPServer udpServer;
	
	private final Thread tcpThread;
	private final Thread udpThread;
	
	/**
	 * Creates, but does not start, 1 {@link network.tcp.TCPServer TCPServer} and 1 
	 * {@link network.udp.UDPServer UDPServer} at the specified ports.
	 * 
	 * @param _tcpPort port for TCP Server
	 * @param _udpPort port for UDP Server
	 */
	public DualServer(int _tcpPort, int _udpPort) {
		tcpPort = _tcpPort;
		udpPort = _udpPort;
		
		tcpServer = new TCPServer(tcpPort, tcpCommunicator);
		udpServer = new UDPServer(udpPort, udpCommunicator);
		
		tcpThread = new Thread(tcpServer, "Skrop TCP Main Server");
		udpThread = new Thread(udpServer, "Skrop UDP Main Server");
	}
	
	/**
	 * Starts the TCP and UDP servers; they will listen on their respective ports until a client connects or sends data.
	 */
	public void start() {
		tcpThread.start();
		udpThread.start();
	}
	
	/**
	 * Sends a message to the TCP Server. If the TCP Server has not been started, 
	 * it will attempt to send the latest message received through this method when it starts.
	 * <p>
	 * The server automatically appends a newline character to the end of the String before sending it.
	 * 
	 * @param message String to send through TCP Server
	 */
	public void sendTCP(String message) {
		tcpCommunicator.send(message);
	}
	
	/**
	 * Sends a message to the UDP Server. If the UDP Server has not been started, 
	 * it will attempt to send the latest message received through this method when it starts.
	 * <p>
	 * The server automatically appends a newline character to the end of the String before sending it.
	 * 
	 * @param message String to send through UDP Server
	 */
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
	
	public Communicator<String> getTCPCommunicator() {
		return tcpCommunicator;
	}
	
	public Communicator<String> getUDPCommunicator() {
		return udpCommunicator;
	}
}
