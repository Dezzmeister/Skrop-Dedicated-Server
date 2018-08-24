package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * It is what it says it is. It handles TCP communication with 1 phone.
 *
 * @author Joe Desmond
 */
public class TCPServer implements Runnable {
	public final int port;
	private Socket socket;
	private ServerSocket serverSocket;
	private final RecentPasser<String> receiver;
	private final RecentPasser<String> sender;
	
	public TCPServer(int _port, RecentPasser<String> _sender, RecentPasser<String> _receiver) {
		port = _port;
		receiver = _receiver;
		sender = _sender;
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		while (true) {
			System.out.println("Waiting for a connection...");
				
			try {
			
				socket = serverSocket.accept();
				System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + " on local port " + socket.getLocalPort() + ".");
			
				var socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				var socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String messageIn = null;
			
				while ((messageIn = socketReader.readLine()) != null) {
					System.out.println("RECEIVED: " + messageIn);
					receiver.pass(messageIn);
					
					/*
					String out = messageIn + " received!";
					socketWriter.write(out);
					socketWriter.write("\n");
					socketWriter.flush();
					*/
					
					if (sender.hasNew()) {
						socketWriter.write(sender.retrieve());
						socketWriter.write("\n");
						socketWriter.flush();
					}
				}
			
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}
