package main;

import network.RecentPasser;
import network.TCPServer;

import network.udp.UDPServer;

public class Main {

	public static void main(String[] args) {
		var sender = new RecentPasser<String>();
		var receiver = new RecentPasser<String>();
		
		var server = new UDPServer(10222, sender, receiver);
		//var server = new network.UDPServer(10222);
		
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		sender.pass("butt");
	}
}
