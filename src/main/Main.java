package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	
	static Server server;
	
	public static void main(String[] args) {
		
		server = new Server(10222, 10223);
		server.start();

		acceptUserInput();
		
		/*
		 * RectangleList rectangles = new RectangleList(10); rectangles.update(0); try {
		 * System.out.println(Serialize.toString(rectangles)); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
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
				if (command.startsWith("tcp send 1") && command.length() >= "tcp send 1".length()) {
					server.server1().sendTCP(command.substring(9));
				} else if (command.startsWith("udp send 1") && command.length() >= "udp send 1".length()) {
					server.server1().sendUDP(command.substring(9));
				} else if (command.startsWith("tcp send 2") && command.length() >= "tcp send 2".length()) {
					server.server2().sendTCP(command.substring(9));
				} else if (command.startsWith("udp send 2") && command.length() >= "udp send 2".length()) {
					server.server2().sendUDP(command.substring(9));
				} else if (command.equals("exit")) {

				}
			}
		}
	}
}
