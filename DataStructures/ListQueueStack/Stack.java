/**
 * 
 * @author Chris Parsons
 * @date 10-03-2023
 * 
 * An easy to use Queue class. Uses the LinkedList class.
 */

public class Stack {
	
	LinkedList list = new LinkedList();
	
	/**
	 * Returns text about the author.
	 * @return Text about the author.
	 */
	public String about() {
		return "Author: Chris Parsons."
				+ "This is a Stack class for easily making and using stacks fast and efficiently.";
	}
	
	/**
	 * Add an item to the head of the stack.
	 * @param item The item to be added.
	 */
	public void Push(String item) {
		list.addHead(item);
	}
	
	/**
	 * Pops (removes) an item from the top of the stack. Note: it returns the value (not the node � that's hidden in the LinkedList class).
	 * @return The removed item
	 */
	public String Pop() {
		return list.removeHead();
	}
	
	/**
	 * Returns true if the stack is empty.
	 * @return True/False if the stack is empty.
	 */
	public Boolean isEmpty() {
		return list.isEmpty();
	}
}