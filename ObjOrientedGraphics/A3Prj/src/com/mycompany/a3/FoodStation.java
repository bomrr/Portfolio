package com.mycompany.a3;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;

/**
 * If the ant touches the food station, add its food to the ant and change the FoodStation's color.
 */
public class FoodStation extends Fixed {
	private int capacity;

	/**
	 * Constructor.
	 * @param size
	 * @param color
	 * @param location
	 * @param foodCapacity
	 */
	public FoodStation(int size, int color, Point location, int foodCapacity) {
		super(size, color, location);
		setFoodCapacity(foodCapacity);
		
		this.setSize(this.getSize() + foodCapacity);
	}
	
	/**
	 * Draws a square of the FoodStation at its position in MapView.
	 */
	@Override
	public void draw(Graphics g, Point pCmpRelPrnt) {
		// Get the object's current location
		int centerX = (int)getLocation().getX();
		int centerY = (int)getLocation().getY();
				
		int topLeftX = centerX - getSize() / 2;
		int topLeftY = centerY - getSize() / 2;
		
		// Set color
		g.setColor(getColor());
		
		// Draw a square at the object's location
		g.drawRect(topLeftX, topLeftY, this.getSize(), this.getSize());
		g.fillRect(topLeftX, topLeftY, this.getSize(), this.getSize());
		
		// Draw  the 'FoodStation' text
		g.setColor(ColorUtil.BLACK);
		g.drawString("" + this.getFoodCapacity(), centerX, centerY);
		
		if (isSelected()) {
			g.setColor(ColorUtil.BLACK);
			g.drawRect(topLeftX, topLeftY, this.getSize(), this.getSize());
		}
	}

	// Getters and Setters =================================
	
	public int getFoodCapacity() {
		return capacity;
	}

	/**
	 * When the food capacity is changed, set color to be lighter or darker.
	 * Lighter when the capacity is lower, higher otherwise.
	 * @param foodCapacity
	 */
	public void setFoodCapacity(int foodCapacity) {
		this.capacity = foodCapacity;
		
		// Dynamically set the color and size of the food station depending on its capacity
		int redBlue = 0;
		redBlue = (foodCapacity * 10);
		
		setColor(Math.min(ColorUtil.rgb(255-redBlue, 255, 255-redBlue), ColorUtil.rgb(175, 255, 175)));
	}

	@Override
	public String toString() {
		return "FoodStation: " + super.toString() + ", foodCapacity=" + capacity;
	}

}
