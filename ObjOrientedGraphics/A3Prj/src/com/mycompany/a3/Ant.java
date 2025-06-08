package com.mycompany.a3;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;

/**
 * The Ant is controlled by the GameWorld, by the player.
 */
public class Ant extends Moveable implements IFoodie {
	private static Ant ant;
	
	private GameWorld gw;
	private int food;
	private int maxSpeed;
	private int foodConsumptionRate;
	private int health;
	private int lastFlagReached = 1; // Default
	private int maxHealth;

	/**
	 * Constructor
	 * @param size
	 * @param color
	 * @param location
	 * @param speed
	 * @param heading
	 * @param food
	 * @param maxSpeed
	 * @param foodConsumptionRate
	 * @param health
	 * @param lastFlagReached
	 */
	private Ant (int size, int color, Point location, int speed, int heading, int food, int maxSpeed, int foodConsumptionRate, int health, int maxHealth, int lastFlagReached) {
		super(size, color, location, speed, heading, lastFlagReached, lastFlagReached);
		this.food = food;
		this.maxSpeed = maxSpeed;
		this.foodConsumptionRate = foodConsumptionRate;
		this.health = health;
		this.lastFlagReached = lastFlagReached;
		this.maxHealth = maxHealth;
	}
	
	/**
	 * Ensures the Ant can only be created once.
	 * @param size
	 * @param color
	 * @param location
	 * @param speed
	 * @param heading
	 * @param food
	 * @param maxSpeed
	 * @param foodConsumptionRate
	 * @param health
	 * @param maxHealth
	 * @param lastFlagReached
	 * @return
	 */
	public static Ant getAnt(int size, int color, Point location, int speed, int heading, int food, int maxSpeed, int foodConsumptionRate, int health, int maxHealth, int lastFlagReached) {
		if (ant == null) {
			ant = new Ant(size, color, location, speed, heading, food, maxSpeed, foodConsumptionRate, health, maxHealth, lastFlagReached);
		}
		return ant;
	}
	
	/**
	 * Remove a life and end the game if all lives are gone.
	 * @param lives
	 */
	public void takeLife(int lives) {
		if (lives == 0) {
			System.out.println("Game Over!");
			System.exit(0);
		}
		
		System.out.println("You died! Lives left: " + lives + ". Resetting health to maximum, food and max speed to 10.");
		
		// Reset attributes
		setFood(10);
		setMaxSpeed(10);
		setHealth(maxHealth);
	}
	
	/**
	 * Move the object a certain amount in the ant's heading.
	 * Ensure food is not 0 first. If so, the ant cannot move.
	 */
	public void moveObject(int toMove, float worldWidth, float worldHeight, int elapsedTime, float worldPositionX, float worldPositionY) {
		if (getFood() <= 0) {
			System.out.println("You cannot move while starving!");
			return;
		}
		
		super.moveObject(toMove, worldWidth, worldHeight, elapsedTime, worldPositionX, worldPositionY);
	}
	
	/**
	 * Update the ant's food according to the incoming food station.
	 * Add the food of the station to the ant's current food.
	 * @param foodStation
	 */
	public void registerFoodStation(FoodStation foodStation) {
		addFood(foodStation.getFoodCapacity());
		
		// Change foodStation color, reduce capacity
		foodStation.setFoodCapacity(0);
	}
	
	/**
	 * Adds or removes food from the current food level.
	 * Does not go below zero.
	 * @param foodLevel
	 */
	public void addFood(int foodLevel) {
		this.food = (Math.max(0, getFood()+foodLevel));
	}
	
	/**
	 * Draws a circle at the position of the ant object in MapView.
	 */
	@Override
	public void draw(Graphics g, Point pCmpRelPrnt) {
		// Get the object's X and Y coordinates
		int centerX = (int)getLocation().getX();
		int centerY = (int)getLocation().getY();
		
		int topLeftX = centerX - getSize() / 2;
		int topLeftY = centerY - getSize() / 2;
		
		g.setColor(getColor());
		
		g.fillArc((int)centerX, (int)centerY, (int) (getSize()/2), (int) (getSize()/2), 0, 360);

		if (isSelected()) {
			g.setColor(ColorUtil.BLACK);
			g.drawRect((int) centerX, (int) centerY, (int) this.getSize()/2, (int) this.getSize()/2);
		}
	}
	
	/**
	 * Defines this object's response to a collision with otherObject
	 * @param otherObject
	 */
	@Override
	public void handleCollision(GameObject otherObject) {
		super.handleCollision(otherObject);
	}
	
	// Getters and setters ==================

	public int getFoodConsumptionRate() {
		return foodConsumptionRate;
	}
	
	@Override
	public void setFoodConsumption(int foodConsumptionRate) {
		this.foodConsumptionRate = foodConsumptionRate;
        if (this.foodConsumptionRate < 0) {
            this.foodConsumptionRate = 0;
        }
	}
	
	public int getFood() {
		return food;
	}
	
	public void setFood(int food) {
		this.food = food;
	}

	public void reduceFood() {
	    this.food = Math.max(0, this.food - this.foodConsumptionRate);
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
		
		if (getSpeed() > getMaxSpeed()) {
			setSpeed(getMaxSpeed());
		}
	}
	
	public int getHealth() {
		return health;
	}

	/**
	 * Set health based on input and dynamically change color and speed.
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = health;
		
		this.setColor(ColorUtil.rgb(255 * health / getMaxHealth(), 255 * health / getMaxHealth(), 255 * health / getMaxHealth()));
		
		// Set maximum speed based on the ant's health / 10.
		setMaxSpeed(getMaxSpeed() * health / getMaxHealth());
		
		System.out.println("Ant health set to: " + getHealth() + ", max speed set to " + getMaxSpeed());
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getLastFlagReached() {
		return lastFlagReached;
	}

	public void setLastFlagReached(int touchedFlag) {
	    if (touchedFlag == this.lastFlagReached + 1) { 
	        this.lastFlagReached = touchedFlag;
	        System.out.println("Ant reached flag: " + touchedFlag);
	        
	    } else {
	        System.out.println("Invalid flag: " + touchedFlag + ". Next flag should be: " + (this.lastFlagReached + 1));
	    }
	}

	@Override
	public String toString() {
		return "Ant: " + super.toString() + ", size=" + getSize() + "\nmaxSpeed=" + getMaxSpeed() + ", foodConsumptionRate: " + getFoodConsumptionRate();
	}
	
}