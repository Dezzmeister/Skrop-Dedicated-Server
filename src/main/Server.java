package main;

import java.util.concurrent.atomic.AtomicBoolean;

import main.game.Game;
import network.DualServer;

public class Server implements Runnable {	
	private final DualServer server1;
	private final DualServer server2;
	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	private final Game game;
	
	/**
	 * Constructs a <code>Server</code> that uses <code>server1Port</code> as both the TCP and UDP port
	 * for the first server, and uses <code>server2Port</code> as both the TCP and UDP port for the
	 * second server.
	 * 
	 * @param server1Port TCP/UDP port for first server
	 * @param server2Port TCP/UDP port for second server
	 */
	public Server(int server1Port, int server2Port) {
		server1 = new DualServer(server1Port, server1Port);
		server2 = new DualServer(server2Port, server2Port);
		
		game = new Game(server1.getTCPCommunicator(), server2.getTCPCommunicator(), server1.getUDPCommunicator(), server2.getUDPCommunicator());
	}
	
	@Override
	public void run() {
		server1.start();
		server2.start();
		
		long last = System.nanoTime();
		double ticks = 60;
		double ns = 1000000000 / ticks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		isRunning.set(true);
		
		while (isRunning.get()) {
			long now = System.nanoTime();
			delta += (now - last) / ns;
			last = now;
			
			game.receiveFromClients();
			
			while (delta >= 1) {
				game.setDelta(delta);
				game.updateGame();
				
				delta--;
			}
			
			game.updateClients();
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}
		}
	}
	
	private void updateClients() {
		
	}
}
