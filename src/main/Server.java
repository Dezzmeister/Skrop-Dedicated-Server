package main;

import network.DualServer;

public class Server {	
	private final DualServer server1;
	private final DualServer server2;
	
	public Server(int server1Port, int server2Port) {
		server1 = new DualServer(server1Port, server1Port);
		server2 = new DualServer(server2Port, server2Port);
	}
}
