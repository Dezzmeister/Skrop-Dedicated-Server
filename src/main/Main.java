package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import network.DualServer;

public class Main {
	static DualServer server1;

	public static void main(String[] args) {
		server1 = new DualServer(10222,10222);
		server1.start();
		
		acceptUserInput();
	}
	
	static void acceptUserInput() {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			String command = "";
			try {
				command = input.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (command != null && !command.equals("")) {		
				if (command.startsWith("tcp send") && command.length() >= 10) {
					server1.sendTCP(command.substring(9));
				} else if (command.startsWith("udp send") && command.length() >= 10) {
					server1.sendUDP(command.substring(9));
				} else if (command.equals("exit")) {
					
				}
			}
		}
	}
}
