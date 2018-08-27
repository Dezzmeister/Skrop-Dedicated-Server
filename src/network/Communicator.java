package network;

/**
 * A two-way variant of {@link RecentPasser}. Like <code>RecentPasser</code>,
 * <code>Communicator</code> is used by {@link network.tcp.TCPServer TCPServers}
 * and {@link network.udp.UDPServer UDPServers} to transfer
 * {@link java.lang.String String} data.
 *
 * @author Joe Desmond
 * @param <T>
 *            The message type.
 */
public class Communicator<T> {
	
	private final RecentPasser<T> sender;
	private final RecentPasser<T> receiver;

	public Communicator(RecentPasser<T> _sender, RecentPasser<T> _receiver) {
		sender = _sender;
		receiver = _receiver;
	}

	public Communicator() {
		sender = new RecentPasser<T>();
		receiver = new RecentPasser<T>();
	}

	/**
	 * Sends a message through the sender.
	 * <p>
	 * <b>Overwrites the previous message sent to the sender.</b>
	 *
	 * @param item
	 *            message to be sent
	 */
	public void send(T item) {
		sender.pass(item);
	}

	/**
	 * Get the latest message from the receiver.
	 *
	 * @return latest message
	 */
	public T receive() {
		return receiver.retrieve();
	}

	/**
	 * Returns <code>true</code> if the receiver has a new message. Does NOT call
	 * <code>RecentPasser.hasNew()</code> on the sender.
	 *
	 * @return true if there is a new message available
	 */
	public boolean hasNew() {
		return receiver.hasNew();
	}

	public RecentPasser<T> getSender() {
		return sender;
	}

	public RecentPasser<T> getReceiver() {
		return receiver;
	}
}
