public class Main {
	public static void main(String[] args) {
		// Create a binary tree.
	    BasicBinaryTree tree = new BasicBinaryTree();
	    BasicBinaryTree.Node root = tree.new Node("Cat",
	    		tree.new Node("Dog",
	    				tree.new Node("Hamster"),
	    				tree.new Node("Mouse")),
	    		tree.new Node("Chicken"));

	    //Test the methods.
	    System.out.println("getTree:");
	    System.out.println(root.getTree(0));
	    System.out.println();
	    
	    System.out.println("getPreOrder:");
		System.out.println(root.getPreOrder());
		System.out.println();
		
		System.out.println("getInOrder:");
		System.out.println(root.getInOrder());
		System.out.println();
		
		System.out.println("getPostOrder:");
		System.out.println(root.getPostOrder());
		System.out.println();
		
		System.out.println("----- New Tree -----");
		
		// Create a binary tree.
	    tree = new BasicBinaryTree();
	    root = tree.new Node("A",
	    		tree.new Node("B",
	    				tree.new Node("D"),
	    				tree.new Node("E")),
	    		tree.new Node("C",
	    				tree.new Node("F",
	    						tree.new Node("H"),
	    						tree.new Node("I")),
	    				tree.new Node("G")));

	    //Test the methods.
	    System.out.println("getTree:");
	    System.out.println(root.getTree(0));
	    System.out.println();
	    
	    System.out.println("getPreOrder:");
		System.out.println(root.getPreOrder());
		System.out.println();
		
		System.out.println("getInOrder:");
		System.out.println(root.getInOrder());
		System.out.println();
		
		System.out.println("getPostOrder:");
		System.out.println(root.getPostOrder());
		System.out.println();
	}
}