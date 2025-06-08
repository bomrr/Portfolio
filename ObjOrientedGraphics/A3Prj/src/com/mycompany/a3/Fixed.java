package com.mycompany.a3;

import com.codename1.charts.models.Point;

/**
 * Fixed objects cannot move.
 */
public abstract class Fixed extends GameObject {

	/**
	 * Constructor.
	 * @param size
	 * @param color
	 * @param location
	 */
	public Fixed(int size, int color, Point location) {
		super(size, color, location);
	}

	/*@Override
	public void setLocation(Point location) {
		System.out.println("Location of a fixed object cannot be changed.");
	}*/
	
	public String toString() {
		return super.toString() + ", size=" + getSize();
		
	}
}