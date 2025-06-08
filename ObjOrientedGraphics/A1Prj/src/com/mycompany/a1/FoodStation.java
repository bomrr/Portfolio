package com.mycompany.a1;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

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
		
		// Dynamically set the color of the food station depending on its capacity
		int redBlue = 0;
		redBlue = (foodCapacity * 10);
		
		setColor(ColorUtil.rgb(redBlue, 255, redBlue));
	}

	@Override
	public String toString() {
		return "FoodStation: " + super.toString() + ", foodCapacity=" + capacity;
	}

}
