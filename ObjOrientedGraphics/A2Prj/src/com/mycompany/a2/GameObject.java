package com.mycompany.a2;

import java.util.ArrayList;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;

/**
 * A game object contains values and represents various entities in the game.
 */
public abstract class GameObject {
	private int size;
	private int color;
	
	private Point location;
	
	/**
	 * Constructor.
	 * @param size
	 * @param color
	 * @param location
	 */
	public GameObject(int size, int color, Point location) {
		super();
		this.size = size;
		this.color = color;
		this.location = location;
	}

	// Getters and Setters =================
	
	public int getSize() {
		return size;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public String toString() {
		return "location=["  + getLocation().getX() + ", " + getLocation().getY() + "]"
				+ ", color=[" + ColorUtil.red(getColor()) + ", " + ColorUtil.green(getColor()) + ", " + ColorUtil.blue(getColor()) + "]";
	}
	
}
