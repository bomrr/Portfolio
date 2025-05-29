/**
 * 
 * @author Chris Parsons
 * @date 11-04-2023
 * 
 * A very basic Binary Tree class made for ease of use.
 */

public class BasicBinaryTree {
	
	/**
	 * A very basic node class for the binary tree that contains the recursive functions for PreOrder, Inorder, PostOrder, and GetTree (give a representation of the tree).
	 * @author Chris Parsons
	 */
	public class Node {
		Node left;
		Node right;
		String value;
		
		Node(String value) {
			this.value = value;
		}
		
		Node(String value, Node left, Node right) {
			this.value = value;
			this.left = left;
			this.right = right;
		}
		
		/**
		 * Returns a string of the values in PreOrder.
		 * Recursive part of the code.
		 * @return Values in PreOrder.
		 */
		String getPreOrder() {
			//Visit the node and get the data
			String result = this.value + " ";
			
			//If left isn't null then left.getPreOrder
			if (left != null) {
				result += left.getPreOrder();
			}
			//If right isn't null then right.getPreOrder
			if (right != null) {
				result += right.getPreOrder();
			}
			
			//Return the result and a space
			return result;
		}
		
		/**
		 * Returns a string of the values in InOrder.
		 * Recursive part of the code.
		 * @return Values in InOrder.
		 */
		String getInOrder() {
			String result = "";
			
			//If left isn't null then left.getPreOrder
			if (left != null) {
				result += left.getInOrder();
			}
			
			//Visit the node and get the data if it is not null
			if (value != null) {
		        result += value + " ";
		    }
			
			//If right isn't null then right.getPreOrder
			if (right != null) {
				result += right.getInOrder();
			}
			
			return result;
		}
		
		/**
		 * Returns a string of the values in PostOrder.
		 * Recursive part of the code.
		 * @return Values in PostOrder.
		 */
		String getPostOrder() {
			String result = "";
			
			//If left isn't null then left.getPreOrder
			if (left != null) {
				result += left.getPostOrder();
			}
			//If right isn't null then right.getPreOrder
			if (right != null) {
				result += right.getPostOrder();
			}
			
			//Return the result and a space
			if (value != null) {
		        result += value + " ";
		    }
			
			return result;
		}
		
		/**
		 * Returns a representation of the tree structure.
		 * Recursive part of the code.
		 * @param indent
		 * @return
		 */
		String getTree(int indent) {
			String result = "";
			
			// Add 3 spaces for every indent.
			for (int i = (indent * 3); i != 0; i--) {
				result += " ";
			}
			
			// Add indicator
			if (value != null) {
		        result += "+-- " + value + "\n";
		    }
			
			// If left isn't null, then result += left.getTree(indent + 1)
			if (left != null) {
				result += left.getTree(indent + 1);
			}
			// If right isn't null, then result += right.getTree(indent + 1)
			if (right != null) {
				result += right.getTree(indent + 1);
			}
			
			return result;
		}
	}
	// ----------- Start of the Binary Tree class -----------
	public Node root;
	
	BasicBinaryTree() {
		this.root = root;
	}
	
	String about() {
		return "Author: Chris Parsons."
				+ "This is a basic Binary Tree class for making and searching through simple trees.";
	}
	
	/**
	 * Returns a string of the values in PreOrder.
	 * @return Values in PreOrder.
	 */
	String getPreOrder() {
		return root.getPreOrder();
	}
	
	/**
	 * Returns a string of the values in InOrder.
	 * @return Values in InOrder.
	 */
	String getInOrder() {
		return root.getInOrder();
	}
	
	/**
	 * Returns a string of the values in PostOrder.
	 * @return Values in PostOrder.
	 */
	String getPostOrder() {
		return root.getPostOrder();
	}
	
	/**
	 * Returns a representation of the tree structure.
	 * Recursive part of the code.
	 * @param i 
	 * @param indent
	 * @return
	 */
	String getTree(int i) {
		return root.getTree(0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}