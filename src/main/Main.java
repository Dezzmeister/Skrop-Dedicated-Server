package main;

import network.RecentPasser;
import network.TCPServer;

public class Main {

	public static void main(String[] args) {
		var sender = new RecentPasser<String>();
		var receiver = new RecentPasser<String>();
		
		TCPServer server = new TCPServer(10222, receiver, sender);
		
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		sender.pass("fatty boy fatty joy");
		while (true) {
			if (sender.retrieved()) {
				sender.pass("crackpot fool");
			}
		}
	}
}
