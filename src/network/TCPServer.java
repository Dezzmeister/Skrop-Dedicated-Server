package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer implements Runnable {
	public final int port;
	private Socket socket;
	private ServerSocket serverSocket;
	
	public TCPServer(int _port) {
		port = _port;
		
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
			
				BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String messageIn = null;
			
				while ((messageIn = socketReader.readLine()) != null) {
					System.out.println("RECEIVED: " + messageIn);
					
					String out = messageIn + " received!";
					socketWriter.write(out);
					socketWriter.write("\n");
					socketWriter.flush();
				}
			
				socket.close();
			} catch (SocketException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}
