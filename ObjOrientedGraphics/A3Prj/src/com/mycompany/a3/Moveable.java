package com.mycompany.a3;

import com.codename1.charts.models.Point;

/**
 * Extends to all movable objects, ensures they have speed, heading, and a movement function.
 */
public abstract class Moveable extends GameObject {
	private int speed;
	private int heading;
	private float worldWidth;
	private float worldHeight;

	/**
	 * Constructor.
	 * @param size
	 * @param color
	 * @param location
	 * @param speed
	 * @param heading
	 */
	public Moveable(int size, int color, Point location, int speed, int heading, float worldWidth, float worldHeight) {
		super(size, color, location);
		this.speed = speed;
		this.heading = heading;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}
	
	/**
	 * Move the object a specified amount along the heading.
	 * @param toMove
	 * @param elapsedTime 
	 */
	public void moveObject(int toMove, float worldWidth, float worldHeight, int elapsedTime, float worldPositionX, float worldPositionY) {
		float angle = (float) Math.toRadians(90 - getHeading());
		
		float deltaX = (float) (Math.cos(angle) * getSpeed() * elapsedTime/1000.0f);
		float deltaY = (float) (Math.sin(angle) * getSpeed() * elapsedTime/1000.0f);
		
		float newX = this.getLocation().getX() + deltaX;
		float newY = this.getLocation().getY() + deltaY;
		
		if (newX > worldWidth) newX = worldWidth;
		if (newY > worldHeight) newY = worldHeight;
		if (newX < 0.0f) newX = 0.0f;
		if (newY < 0.0f) newY = 0.0f;
		
		setLocation(new Point(newX, newY));
	}
	
	// Getters and Setters ================

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getHeading() {
		return heading;
	}

	/**
	 * Set the heading to the incoming one, ensuring that it remains within bounds.
	 * Minimum heading is 0, maximum 360.
	 * @param newHeading
	 */
	public void setHeading(int newHeading) {
		if (newHeading <= 0) {
			newHeading = (newHeading + 360) % 360;
		}
		if (newHeading >= 360) {
			newHeading -= 360;
		}
		
		this.heading = newHeading;
	}

	@Override
	public String toString() {
		return super.toString() + ", heading=" + heading + ", speed=" + speed;
	}
}
