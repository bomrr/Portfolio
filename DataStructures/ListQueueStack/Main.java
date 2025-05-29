/**
 * 
 * @author Chris Parsons
 * @date 10-03-2023
 * 
 * Tests the LinkedList, Node, Stack, and Queue classes.
 */

public class Main {
	public static void main(String[] args) {
		
		LinkedList list = new LinkedList();
		
		Stack stack = new Stack();
		
		Queue queue = new Queue();
		
		// Testing Linked List
		list.about();
		System.out.println("Linked List Testing:");
		System.out.println("List is empty: " + list.isEmpty());
		list.addTail("A");
		list.addHead("b");
		list.addTail("C");
		list.addHead("D");
		list.printList();
		System.out.println("List is empty: " + list.isEmpty());
		
		list.removeHead();
		list.removeHead();
		list.removeHead();
		list.removeHead();
		list.addHead("X");
		list.addHead("S");
		list.printList();
		
		// Testing stack
		System.out.println("Stack testing:");
		stack.Push("A");
		stack.Push("B");
		System.out.print(stack.Pop() + ", ");
		System.out.println(stack.Pop());
		System.out.println("Stack is empty: " + stack.isEmpty());
		
		stack.Push("A");
		stack.Push("B");
		stack.Push("C");
		stack.Push("D");
		System.out.print(stack.Pop() + ", ");
		System.out.println(stack.Pop());
		System.out.println("Stack is empty: " + stack.isEmpty());
		
		//Testing Queue
		System.out.println("Testing Queue:");
		System.out.println("Queue is empty: " + queue.isEmpty());
		queue.Enqueue("A");
		queue.Enqueue("B");
		queue.Enqueue("C");
		queue.Enqueue("D");
		System.out.print(queue.Dequeue() + ", ");
		System.out.println(queue.Dequeue());
		
		queue.Enqueue("Z");
		System.out.print(queue.Dequeue() + ", ");
		System.out.print(queue.Dequeue() + ", ");
		queue.Enqueue("A");
		System.out.println(queue.Dequeue());
		System.out.println("Queue is empty: " + queue.isEmpty());
	}
}