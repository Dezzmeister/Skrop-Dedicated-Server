package network;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to pass messages from a Server's client to game logic code, and to send messages from game logic code through a Server to a client. Only retains most recent message.
 *
 * @author Joe Desmond
 */
public class RecentPasser<T> {
	private T received;
	private final AtomicBoolean retrieved = new AtomicBoolean(false);
	private final AtomicBoolean changed = new AtomicBoolean(false);
	
	public boolean hasNew() {
		
		boolean result = changed.get();
		
		while (!changed.compareAndSet(result, false));

		return result;
	}
	
	/**
	 * Updates this Passer's object.
	 * 
	 * @param object Object to be passed
	 */
	public void pass(T object) {
		received = object;
		
		boolean expected = retrieved.get();
		
		while (!retrieved.compareAndSet(expected, false));
		
		expected = changed.get();
		
		while (!changed.compareAndSet(expected, true));
	}
	
	/**
	 * Returns the current object of type T held by this Passer.
	 * 
	 * @return object to be passed
	 */
	public T retrieve() {
		
		boolean expected = retrieved.get();
		
		while (!retrieved.compareAndSet(expected, true));
		
		return received;
	}
	
	public boolean retrieved() {
		return retrieved.get();
	}
}
