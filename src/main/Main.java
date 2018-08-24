package main;

import network.DualServer;

public class Main {

	public static void main(String[] args) {
		DualServer server1 = new DualServer(10222,10222);
		server1.start();
	}
}
