package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Sends and receives TCP packets in a connection with one other device. When <code>run()</code> is called, this <code>TCPServer</code> begins listening on the
 * port specified in the constructor. When another device successfully connects, communication can begin.
 * 
 * <p>
 * Unlike UDP, two devices communicating through TCP hold a formal connection. This means that separate classes are not needed to send or receive TCP packets.
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
		
		createSocket();
	}
	
	public TCPServer(int _port, Communicator<String> communicator) {
		port = _port;
		sender = communicator.sender;
		receiver = communicator.receiver;
		
		createSocket();
	}
	
	/**
	 * <b>DO NOT EXPLICITLY CALL THIS METHOD. USE THIS TCP SERVER IN A THREAD.</b>
	 * <p>
	 * <b>THIS METHOD BLOCKS INDEFINITELY.</b>
	 */
	@Override
	public void run() {
		
		while (true) {
			System.out.println("TCP Server listening at TCP port " + port + ". Waiting for a connection...");
				
			try {
			
				socket = serverSocket.accept();
				System.out.println("TCP Server connected to " + socket.getInetAddress().getHostAddress() + " on local port " + socket.getLocalPort() + ".");
			
				var socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				var socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String messageIn = null;
				
				while ((messageIn = socketReader.readLine()) != null) {
					System.out.println("TCP RECEIVED: " + messageIn);
					receiver.pass(messageIn);
					
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
	
	private void createSocket() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("TCP Server socket created at TCP port " + port + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
