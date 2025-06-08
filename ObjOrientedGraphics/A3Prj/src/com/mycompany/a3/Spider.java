package com.mycompany.a3;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;

/**
 * The spider moves around the screen in random directions.
 */
public class Spider extends Moveable {

	public Spider(int size, int color, Point location, int speed, int heading, float worldWidth, float worldHeight) {
		super(size, color, location, speed, heading, worldWidth, worldHeight);
	}

	/**
	 * Draws an upside-down triangle at the flag's location with the flag number
	 * displayed.
	 */
	@Override
	public void draw(Graphics g, Point pCmpRelPrnt) {
		// Get the object's current location
		int centerX = (int) getLocation().getX();
		int centerY = (int) getLocation().getY();

		int topLeftX = centerX - getSize() / 2;
		int topLeftY = centerY - getSize() / 2;

		// Set color
		g.setColor(getColor());

		int[] xPoints = {
				topLeftX,
				topLeftX + getSize(),
				topLeftX + (getSize())/2
		};
		int[] yPoints = {
				topLeftY,
				topLeftY,
				topLeftY + (getSize())
		};

		g.drawPolygon(xPoints, yPoints, 3);

		if (isSelected()) {
			g.drawRect(topLeftX, topLeftY, this.getSize(), this.getSize());
		}
	}

	/**
	 * Defines this object's response to a collision with otherObject
	 * 
	 * @param otherObject
	 */
	@Override
	public void handleCollision(GameObject otherObject) {
		super.handleCollision(otherObject);
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