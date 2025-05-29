/**
 * 
 * @author Chris Parsons
 * @date 10-03-2023
 * 
 * A Linked List class made for ease of use.
 */



public class LinkedList {
	
	
	/**
	 * A node class for the linked list to use.
	 * Stores data in each node.
	 * @author Chris Parsons
	 *
	 */
	private static class Node {
		String data;// Data stored in the nodes
	    Node next;// A link to the next node in the list
	    
	    /**
	     * @param value The inputed value to be placed into the linked list.
	     */
	    public Node(String value) {
	    	this.next = null;
	    	this.data = value;
	    }
	    
	    /**
	     * Prints the list starting from the head using recursion.
	     */
	    public void printList() {
	    	System.out.print(this.data + " ");
	    	
	    	if (this.next != null) {
	    		this.next.printList();
	    	}
	    }
	}
	
	Node head;// The start of the list
	Node tail;// The end of the list
	
	/**
	 * Constructor for the linked list.
	 */
	public LinkedList() {
		head = null;
		tail = null;
	}
	
	/**
	 * @return Returns data on the author.
	 */
	public String about() {// Returns text about me
		return "Author: Chris Parsons."
				+ "This is a Linked List class for easily making and using linked lists fast and efficiently.";
	}
	
	/**
	 * Adds an object to the start of the list
	 * @param value The opject to be added
	 */
	public void addHead(String value) {
		Node addNode = new Node(value);
		
		// If the head is empty, fill it with the new node
		if (isEmpty()) {
			head = addNode;
			tail = addNode;
			return;
		}
		// Otherwise, add the new node and set it as the head, link it to the head and tail
		else {
			addNode.next = head;
			head = addNode;
		}
	}
	
	/**
	 * Adds an object to the end of the list
	 * @param value The object to be added
	 */
	public void addTail(String value) {
		Node addNode = new Node(value);
		
		// If the tail is null, place the value inside instead of making a new node
		if (isEmpty()) {
			head = addNode;
			tail = addNode;
		}
		// Otherwise, add the node and set it as the tail.
		else {
			tail.next = addNode;
			tail = addNode;
		}
	}
	
	/**
	 * Removes the first object from the start of the list
	 * @return A string of what was removed, or 1 if the head is empty
	 */
	public String removeHead() {
		
		// If the head is null, return 1
		if (isEmpty()) {
			return "1";
		}
		
		String deletedData = head.data;// Store the deleted data so it can be returned
		
		// If the head and the tail are the same, de-reference both
		if (head == tail) {
			head = null;
			tail = null;
		}
		// Remove the head from the data
		else {
		head = head.next; //Link new node
		}
		
		return deletedData;
	}
	
	/**
	 * Returns true if the list is empty.
	 * @return True or False, true if head is empty, false otherwise.
	 */
	public Boolean isEmpty() {
		// Return true if there is no head, false otherwise
		return head == null;
	}
	
	/**
	 * Goes through each object in the list, printing as it goes.
	 */
	public void printList() {
		head.printList();
		System.out.println();
	}
}