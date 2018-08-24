package main;

import network.RecentPasser;
import network.UDPServer;

public class Main {

	public static void main(String[] args) {
		var sender = new RecentPasser<String>();
		var receiver = new RecentPasser<String>();
		
		UDPServer server = new UDPServer(10222);
		
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
}
