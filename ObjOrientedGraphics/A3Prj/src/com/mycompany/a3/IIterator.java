package com.mycompany.a3;

/**
 * Applies basic Iterator methods to an iterator.
 */
public interface IIterator {
	
	public boolean hasNext();
	
	public GameObject getNext();
	
	public void reset();
}
