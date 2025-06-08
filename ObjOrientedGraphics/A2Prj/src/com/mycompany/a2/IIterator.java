package com.mycompany.a2;

/**
 * Applies basic Iterator methods to an iterator.
 */
public interface IIterator {
	
	public boolean hasNext();
	
	public GameObject getNext();
	
	public void reset();
}
