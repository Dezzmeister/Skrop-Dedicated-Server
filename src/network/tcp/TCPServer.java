package network.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import network.Communicator;
import network.RecentPasser;

/**
 * Sends and receives TCP packets in a connection with one other device. When <code>run()</code> is called, this <code>TCPServer</code> begins listening on the
 * port specified in the constructor. When another device successfully connects, communication can begin.
 * 
 * <p>
 * Unlike UDP, two devices communicating through TCP hold a formal connection.
 *
 * @author Joe Desmond
 */
public class TCPServer implements Runnable {
	public final int port;
	private Socket socket;
	private ServerSocket serverSocket;
	private final RecentPasser<String> receiver;
	private final RecentPasser<String> sender;
	
	/**
	 * Creates a <code>TCPServer</code> that will send and receive data through the local TCP port, <code>_port</code>. 
	 * Received data will be available in <code>_receiver</code>; data can be sent through <code>_sender</code>.
	 * 
	 * @param _port TCP port to listen on
	 * @param _sender used to send data to the other device
	 * @param _receiver used to receive data from the other device
	 */
	public TCPServer(int _port, RecentPasser<String> _sender, RecentPasser<String> _receiver) {
		port = _port;
		receiver = _receiver;
		sender = _sender;
		
		createSocket();
	}
	
	/**
	 * Creates a <code>TCPServer</code> that will send and receive data through the local TCP port, <code>_port</code>. 
	 * Received data will be available in <code>communicator</code>, which can also be used to send data.
	 * 
	 * @param _port TCP port to listen on
	 * @param communicator <code>Communicator</code> to send and receive String data
	 */
	public TCPServer(int _port, Communicator<String> communicator) {
		port = _port;
		sender = communicator.getSender();
		receiver = communicator.getReceiver();
		
		createSocket();
	}
	
	/**
	 * Creates a new <code>TCPServer</code> with the specified port and <code>Communicator</code>.
	 * Returns a new <code>Thread</code> with this <code>TCPServer</code>, but does not start the <code>Thread</code>.
	 * 
	 * @param port port to create <code>TCPServer</code> with
	 * @param communicator <code>Communicator</code> to create <code>TCPServer</code> with
	 * @return <code>Thread</code> to run newly created <code>TCPServer</code>
	 */
	public static Thread createOnNewThread(int port, Communicator<String> communicator) {
		return new Thread(new TCPServer(port, communicator));
	}
	
	private BufferedReader socketReader;
	private BufferedWriter socketWriter;
	
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
			
				socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				while (socket.isConnected()) {
					String messageIn = null;
				
					while (socketReader.ready() && (messageIn = socketReader.readLine()) != null) {
						System.out.println("TCP RECEIVED: " + messageIn);
						receiver.pass(messageIn);
					}
				
					if (sender.hasNew()) {
						socketWriter.write(sender.retrieve());
						socketWriter.write("\n");
						socketWriter.flush();
					}
				}
			
				socket.close();
				receiver.pass(null);
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
