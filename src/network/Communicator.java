package network;

/**
 * A two-way variant of {@link RecentPasser}.
 *
 * @author Joe Desmond
 * @param <T>
 */
public class Communicator<T> {
	public final RecentPasser<T> sender;
	public final RecentPasser<T> receiver;
	
	public Communicator(RecentPasser<T> _sender, RecentPasser<T> _receiver) {
		sender = _sender;
		receiver = _receiver;
	}
	
	public Communicator() {
		sender = new RecentPasser<T>();
		receiver = new RecentPasser<T>();
	}
	
	public void send(T item) {
		sender.pass(item);
	}
	
	public T receive() {
		return receiver.retrieve();
	}
	
	/**
	 * Returns <code>true</code> if the receiver has a new message. 
	 * Does NOT call <code>RecentPasser.hasNew()</code> on the sender.
	 * 
	 * @return
	 */
	public boolean hasNew() {
		return receiver.hasNew();
	}
}
