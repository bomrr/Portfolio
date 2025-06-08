package com.mycompany.a3;

import java.util.ArrayList;

/**
 * A system that stores GameObjects.
 * Contains an internal Iterator system that extends IIterator.
 */

public class GameObjectCollection implements ICollection {
	private ArrayList<GameObject> container = new ArrayList<GameObject>();
	private Iterator gameObs;
	
	/**
	 * Constructor
	 */
	public GameObjectCollection() {
		gameObs = new Iterator();
	}

	/**
	 * Inner Iterator for holding a collection of objects.
	 * hasNext() to check if the next object exists.
	 * getNext() to traverse through the iterator and return objects. If it hits the end of the iterator, it returns to item 0.
	 * addObject() adds another object to the iterator and adds one to the size container.
	 * getSize() returns the number of items in the iterator.
	 * reset() returns the index of the iterator to -1, the end/beginning of the list.
	 */
	private class Iterator implements IIterator {
		private int index;
		
		public Iterator() {
			// Using negative one to represent the end of the list
			setIndex(-1);
		}

		/**
		 * Check if the next object exists.
		 */
		@Override
		public boolean hasNext() {
			if (getIndex() + 1 < getSize()) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Traverse through the iterator and return objects.
		 * If it hits the end of the iterator, it returns to item 0.
		 */
		@Override
		public GameObject getNext() {
			if (!hasNext()) {
				setIndex(-1);
			} else {
				setIndex(getIndex() + 1);
			}
			
			return container.get(index);
		}
		
		/**
		 * Reset the Iterator's index location.
		 */
		public void reset() {
			setIndex(-1);
		}

		private ArrayList<GameObject> getContainer() {
			return container;
		}

		public int getSize() {
			return container.size();
		}

		private int getIndex() {
			return index;
		}

		private void setIndex(int index) {
			this.index = index;
		}
		
	}
	
	/**
	 * Add an object to the collection.
	 */
	@Override
	public void add(GameObject gameObject) {
		container.add(gameObject);
	}

	/**
	 * Return the iterator.
	 * @return IIterator
	 */
	@Override
	public IIterator getIterator() {
		return gameObs;
	}
}
