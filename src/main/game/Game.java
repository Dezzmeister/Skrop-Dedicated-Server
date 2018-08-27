package main.game;

import main.state.GameState;
import network.Communicator;

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
	
	private double delta;
	
	public Game(Communicator<String> _server1TCP, Communicator<String> _server2TCP, Communicator<String> _server1UDP, Communicator<String> _server2UDP) {
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
		switch (gameState) {
			case INACTIVE:
				updateInactive();
				break;
			case WAITING_FOR_CONNECTION_2:
				updateWaitingForConnection2();
				break;
		}
	}
	
	private void updateInactive() {
		if (fromClient1TCP != null ^ fromClient2TCP != null) {
			gameState = GameState.WAITING_FOR_CONNECTION_2;
			
			if (fromClient1TCP != null) {
				toClient1TCP = "waiting";
			}
			if (fromClient2TCP != null) {
				toClient2TCP = "waiting";
			}
		}
	}
	
	private void updateWaitingForConnection2() {
		
	}
}
