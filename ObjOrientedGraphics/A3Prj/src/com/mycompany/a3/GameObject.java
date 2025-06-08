package com.mycompany.a3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;

/**
 * A game object contains values and represents various entities in the game.
 */
public abstract class GameObject implements IDrawable, ICollider, ISelectable {
	private int size;
	private int color;
	private Vector<GameObject> collisionVector = new Vector();
	private boolean isSelected;

	private Point location;

	/**
	 * Constructor.
	 * 
	 * @param size
	 * @param color
	 * @param location
	 */
	public GameObject(int size, int color, Point location) {
		super();
		this.setSize(size);
		this.color = color;
		this.location = location;
	}

	/**
	 * Draws the object on the screen.
	 * @return
	 */
	public abstract void draw(Graphics g, Point pCmpRelPrnt);

	public boolean contains(Point pPtrRelPrnt, Point pCmpRelPrnt) {
		int px = (int) pPtrRelPrnt.getX();
		int py = (int) pPtrRelPrnt.getY();

		int xLoc = (int) getLocation().getX() - getSize() / 2;
		int yLoc = (int) getLocation().getY() - getSize() / 2;
		
		// For testing 
		//System.out.println("Contains: " + px + ", " + py + ": " + this.toString());

		if ((px >= xLoc) && (px <= xLoc + this.getSize()) && (py >= yLoc) && (py <= yLoc + this.getSize()))
			return true;
		else
			return false;
	}

	/**
	 * Returns true if this object is colliding with the inputed object.
	 * 
	 * @param otherObject
	 */
	public boolean collidesWith(GameObject otherObject) {
		boolean result = false;
		int thisCenterX = (int) (this.getLocation().getX() + (this.getSize() / 2));
		int thisCenterY = (int) (this.getLocation().getY() + (this.getSize() / 2));
		int otherCenterX = (int) (otherObject.getLocation().getX() + (otherObject.getSize() / 2));
		int otherCenterY = (int) (otherObject.getLocation().getY() + (otherObject.getSize() / 2));

		// find dist between centers (use square, to avoid taking roots)
		int dx = thisCenterX - otherCenterX;
		int dy = thisCenterY - otherCenterY;
		int distBetweenCentersSqr = (dx * dx + dy * dy);

		// find square of sum of radii
		int thisRadius = this.getSize() / 2;
		int otherRadius = otherObject.getSize() / 2;
		int radiiSqr = (thisRadius * thisRadius + 2 * thisRadius * otherRadius + otherRadius * otherRadius);

		if (distBetweenCentersSqr <= radiiSqr) {
			result = true;
		}
		return result;
	}

	/**
	 * Defines this object's response to a collision with otherObject
	 * 
	 * @param otherObject
	 */
	public void handleCollision(GameObject otherObject) {
		// If the object has been collided with add it to the vector
		if (!this.getCollisionVector().contains(otherObject)) {
			getCollisionVector().add(otherObject);

			// For testing
			// System.out.println("Collision with " + otherObject.toString());
		}

		// If the object is not colliding with an object, remove it
		ArrayList<GameObject> toRemove = new ArrayList<>();

		for (GameObject currentObject : getCollisionVector()) {
			if (!this.collidesWith(currentObject) && currentObject != this) {
				toRemove.add(currentObject);
			}
		}
		getCollisionVector().removeAll(toRemove);

		// For testing
		System.out.println("Collision vector contains: " + collisionVector.toString());
	}

	// Getters and Setters =================

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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
		return "location=[" + getLocation().getX() + ", " + getLocation().getY() + "]" + ", color=["
				+ ColorUtil.red(getColor()) + ", " + ColorUtil.green(getColor()) + ", " + ColorUtil.blue(getColor())
				+ "]";
	}

	public Vector<GameObject> getCollisionVector() {
		return collisionVector;
	}

	public void setCollisionVector(Vector<GameObject> colisionVector) {
		this.collisionVector = colisionVector;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
