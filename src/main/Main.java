package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import main.game.RectangleList;
import network.DualServer;
import network.Serialize;

public class Main {
	
	static DualServer server1;
	
	public static void main(String[] args) {
		/*
		server1 = new DualServer(10222, 10222);
		server1.start();

		acceptUserInput();
		*/
		
		RectangleList rectangles = new RectangleList(10);
		rectangles.update(0);
		try {
			System.out.println(Serialize.toString(rectangles));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
