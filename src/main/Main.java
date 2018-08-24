package main;

import network.TCPServer;

public class Main {

	public static void main(String[] args) {
		TCPServer server = new TCPServer(10222);
		
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
}
