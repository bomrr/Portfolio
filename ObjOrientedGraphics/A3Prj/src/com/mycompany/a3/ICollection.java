package com.mycompany.a3;

/**
 * ICollection to identify a collection object.
 */
public interface ICollection {
	
	public IIterator getIterator();
	
	void add(GameObject object);
}
