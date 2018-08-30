package main.game;

import java.io.IOException;

import main.state.GameState;
import network.Communicator;
import network.Serialize;

public class Game {
	
	private GameState gameState = GameState.INACTIVE;

	private final Communicator<String> server1TCP;
	private final Communicator<String> server2TCP;
	private final Communicator<String> server1UDP;
	private final Communicator<String> server2UDP;

	private String fromClient1TCP;
	private String fromClient2TCP;

	private String fromClient1UDP;
	private String fromClient2UDP;
	/**
	 *
	 *
	 */
	private String toClient1TCP;
	private String toClient2TCP;

	private String toClient1UDP;
	private String toClient2UDP;

	private double delta = 1;
	
	private boolean client1Ready = false;
	private boolean client2Ready = false;
	
	private int score1 = 0;
	private int score2 = 0;
	
	private final RectangleList rectangles = new RectangleList(10);

	public Game(Communicator<String> _server1TCP, Communicator<String> _server2TCP, Communicator<String> _server1UDP,
			Communicator<String> _server2UDP) {
		server1TCP = _server1TCP;
		server2TCP = _server2TCP;
		server1UDP = _server1UDP;
		server2UDP = _server2UDP;
	}

	public void receiveFromClients() {
		fromClient1TCP = server1TCP.receive();
		fromClient2TCP = server2TCP.receive();
		fromClient1UDP = server1UDP.receive();
		fromClient2UDP = server2UDP.receive();
	}

	public void updateClients() {
		server1TCP.send(toClient1TCP);
		server2TCP.send(toClient2TCP);
		server1UDP.send(toClient1UDP);
		server2UDP.send(toClient2UDP);
	}

	public void setDelta(double _delta) {
		delta = _delta;
	}

	public void updateGame() {
		receiveFromClients();

		switch (gameState) {
			case INACTIVE:
				updateInactive();
				break;
			case WAITING_FOR_CONNECTION_2:
				updateWaitingForConnection2();
				break;
			case WAITING_FOR_GAME_START:
				updateWaitingForGameStart();
				break;
			case IN_GAME:
				updateInGame();
				break;
			case END_GAME:
				updateEndGame();
				break;
		}
	}
	
	private void updateInactive() {
		
		if (fromClient1TCP != null ^ fromClient2TCP != null) {
			gameState = GameState.WAITING_FOR_CONNECTION_2;

			if (fromClient1TCP != null) {
				server1TCP.send("waiting, client 1");
			}
			if (fromClient2TCP != null) {
				server2TCP.send("waiting, client 2");
			}
		}
	}

	private void updateWaitingForConnection2() {
		if (fromClient1TCP != null && fromClient2TCP != null) {
			gameState = GameState.WAITING_FOR_GAME_START;

			server1TCP.send("waiting for game");
			server2TCP.send("waiting for game");
		}
	}

	private void updateWaitingForGameStart() {
		rectangles.update(0);
		
		if (fromClient1TCP.equals("ready")) {
			client1Ready = true;
		}
		
		if (fromClient2TCP.equals("ready")) {
			client2Ready = true;
		}
		
		if (client1Ready && client2Ready) {
			try {
				String world = "world " + Serialize.toString(rectangles);
				server1TCP.send(world);
				server2TCP.send(world);
			} catch (IOException e) {
				server1TCP.send("error Server-side error occurred; critical game data could not be sent.");
				server2TCP.send("error Server-side error occurred; critical game data could not be sent.");
				e.printStackTrace();
			}

			gameState = GameState.IN_GAME;
		}
	}
	
	private void updateInGame() {
		boolean needToUpdateClients = false;
		
		if (server1TCP.hasNew() && fromClient1TCP.startsWith("mouse ")) {
			float x = Float.parseFloat(fromClient1TCP.substring(6, fromClient1TCP.indexOf(",")));
			float y = Float.parseFloat(fromClient1TCP.substring(fromClient1TCP.indexOf(",")));
			
			Rectangle rect = rectangles.successfulHit(x, y);
			
			if (rect != null) {
				needToUpdateClients = true;
				score1 += rect.maxWidth / ((rect.width == 0) ? 1 : rect.width);
			}
		}
		
		if (server2TCP.hasNew() && fromClient2TCP.startsWith("mouse ")) {
			float x = Float.parseFloat(fromClient2TCP.substring(6, fromClient2TCP.indexOf(",")));
			float y = Float.parseFloat(fromClient2TCP.substring(fromClient2TCP.indexOf(",")));
			
			Rectangle rect = rectangles.successfulHit(x, y);
			
			if (rect != null) {
				needToUpdateClients = true;
				score2 += rect.maxWidth / ((rect.width == 0) ? 1 : rect.width);
			}
		}
		
		rectangles.update(1);
		
		//A world sync needs to be performed because a rectangle has been destroyed
		if (needToUpdateClients) {
			try {
				String data = "sync s1:" + score1 + "s2:" + score2 + "w:" + Serialize.toString(rectangles);
				server1TCP.send(data);
				server2TCP.send(data);
			} catch (IOException e) {
				server1TCP.send("error Server-side error occurred; critical game data could not be sent.");
				server2TCP.send("error Server-side error occurred; critical game data could not be sent.");
				e.printStackTrace();
			}
		}
		
		if (server1TCP.hasNew() && fromClient1TCP.equals("quit")) {
			gameState = GameState.QUIT_GAME;
		}
		
		if (server2TCP.hasNew() && fromClient2TCP.equals("quit")) {
			gameState = GameState.QUIT_GAME;
		}
	}
	
	private void updateEndGame() {
		
	}
}
