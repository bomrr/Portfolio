/**
 * 
 * @author Chris Parsons
 * @date 10-03-2023
 * 
 * An easy to use Queue class. Uses the LinkedList class.
 */

public class Queue {
	
	LinkedList list = new LinkedList();
	
	/**
	 * Returns text about the author.
	 * @return Text about the author.
	 */
	public static String about() {
		return "Author: Chris Parsons."
				+ "This is a Queue class for easily making and using queues fast and efficiently.";
	}
	
	/**
	 * Add an an item into the queue.
	 * @param item The item to be added.
	 */
	public void Enqueue(String item) {
		list.addTail(item);
	}
	
	/**
	 * Uses the list removeHead to remove the latest add to the queue.
	 * @return
	 */
	public String Dequeue() {
		return list.removeHead();
	}
	
	/**
	 * Uses the list's isEmpty method, returns true if the head is empty.
	 * @return True if the head is empty
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
}