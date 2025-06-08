package com.mycompany.a2;

import com.codename1.charts.models.Point;

/**
 * The spider moves around the screen in random directions.
 */
public class Spider extends Moveable {

	public Spider(int size, int color, Point location, int speed, int heading) {
		super(size, color, location, speed, heading);
	}

	@Override
	public String toString() {
		return "Spider: " + super.toString() + ", size=" + getSize();
	}
	
	@Override
	public void setColor(int color) {
		System.out.println("Spider color does not change.");
	}

}