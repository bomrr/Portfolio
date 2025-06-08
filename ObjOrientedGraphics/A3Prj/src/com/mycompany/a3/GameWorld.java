package com.mycompany.a3;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;

import com.codename1.charts.models.Point;
import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.util.UITimer;

/**
 * Control all objects within the game.
 * 
 * GameWorld is observable, meaning it contains data that changes regularly.
 */
public class GameWorld extends Observable {

	// Collection of game objects
	private GameObjectCollection objectCollection = new GameObjectCollection();
	private IIterator gameObs;

	private int gameTick = 0;
	private int lives = 3;
	private boolean sound = false;
	private float worldWidth = 1000.0f; // Default
	private float worldHeight = 1000.0f; // Default
	private float worldPositionX = 0.0f;
	private float worldPositionY = 0.0f;
	
	private boolean isPaused = false;
	
	private int[] lastPointClicked = {-1, -1};
	private boolean selectedMove = false;
	
	// Sounds; the slides show each sound as being global
	// Don't need getters and setters as they should only be used here
	private Sound squeakSound;
	private Sound flagSound;
	private Sound foodSound;
	
	private BGSound constSound;

	public GameWorld() {
		gameObs = objectCollection.getIterator();
	}

	/**
	 * Method init() is responsible for creating the initial state of the world.
	 * This should include adding to the game world at least the following: a
	 * minimum of four Flag objects, positioned and sized as you choose and numbered
	 * sequentially defining the path (you may add more than four initial flags if
	 * you like - maximum number of flags you can add is nine); one Ant, initially
	 * positioned at the flag #1 with initial heading which is zero, initial
	 * positive non-zero speed as you choose, and initial size as you choose; at
	 * least two Spider objects, randomly positioned with a randomly-generated size,
	 * heading, and speed; and at least two FoodStation objects with random
	 * locations and with random sizes.
	 * 
	 * Also adds each GameObject to the iterator collection and prepares them for
	 * use in game.
	 */
	public void init() {
		Random rand = new Random();
		Point firstFlagSpot = new Point(getWorldWidth() / 5, getWorldHeight() / 4);

		// Create at least 4 flags
		// int size, int color, Point location, int flagNum
		Flag flag1 = new Flag(80, ColorUtil.rgb(144, 202, 249), firstFlagSpot, 1);
		Flag flag2 = new Flag(80, ColorUtil.rgb(144, 202, 249), new Point(getWorldWidth() / 1.5f, getWorldHeight() / 2), 2);
		Flag flag3 = new Flag(80, ColorUtil.rgb(144, 202, 249), new Point(getWorldWidth() / 1.8f, getWorldHeight() / 2.5f), 3);
		Flag flag4 = new Flag(80, ColorUtil.rgb(144, 202, 249), new Point(getWorldWidth() / 1.25f, getWorldHeight() / 1.05f), 4);
		objectCollection.add(flag1);
		objectCollection.add(flag2);
		objectCollection.add(flag3);
		objectCollection.add(flag4);

		// Create Ant
		// int size, int color, Point location, int speed, int heading, int food, int maxSpeed, int foodConsumptionRate, int health, int maxHealth, int lastFlagReached
		Ant ant = Ant.getAnt(80, ColorUtil.rgb(255, 0, 0), firstFlagSpot, 30, 0, 80, 60, 1, 10, 10, 1);
		objectCollection.add(ant);

		// Create two spiders
		// int size, int color, Point location, int speed, int heading, int worldWidth, int worldHeight
		Spider spider1 = new Spider(rand.nextInt(30) + 70, ColorUtil.BLACK, getRandomLocation(), rand.nextInt(20) + 30, rand.nextInt(360), getWorldWidth(), getWorldHeight());
		Spider spider2 = new Spider(rand.nextInt(30) + 70, ColorUtil.BLACK, getRandomLocation(), rand.nextInt(20) + 30, rand.nextInt(360), getWorldWidth(), getWorldHeight());
		objectCollection.add(spider1);
		objectCollection.add(spider2);

		// Create two FoodStations
		// int size, int color, Point location, int foodCapacity
		FoodStation foodStation1 = new FoodStation(rand.nextInt(10) + 40, ColorUtil.rgb(0, 255, 0), getRandomLocation(), rand.nextInt(10) + 10);
		FoodStation foodStation2 = new FoodStation(rand.nextInt(10) + 40, ColorUtil.rgb(0, 255, 0), getRandomLocation(), rand.nextInt(10) + 10);
		objectCollection.add(foodStation1);
		objectCollection.add(foodStation2);

		notifyObservers();
	}

	/**
	 * 'a' - tell the game world to accelerate (increase the speed of) the ant by a
	 * small amount. Speed is effected by food and health.
	 */
	public void accelerate() {
		Ant ant = getAntObject();

		// Either up the ant's speed by two, or the max speed, depending on which is lower.
		int newSpeed = Math.min(ant.getSpeed() + 2, ant.getMaxSpeed());
		ant.setSpeed(newSpeed);

		System.out.println("Accelerating ant to speed " + ant.getSpeed() + ", Position: " + ant.getLocation().getX()
				+ ", " + ant.getLocation().getY());

		setChanged();
		notifyObservers();
	}

	/**
	 * 'b' - Tell the game world to brake by a small amount. Ensure minimum speed is
	 * 0.
	 */
	public void brake() {
		Ant ant = getAntObject();

		int newSpeed = Math.min(ant.getSpeed() - 2, ant.getMaxSpeed());

		if (newSpeed <= 0) {
			newSpeed = 0;
		} else {
			ant.setSpeed(newSpeed);
		}
		System.out.println("Braking ant to speed " + newSpeed + " Position: " + ant.getLocation().getX() + ", "
				+ ant.getLocation().getY());

		setChanged();
		notifyObservers();
	}

	/**
	 * 'l' - Change the ant's heading 5 degrees to the left.
	 */
	public void headLeft() {
		Ant ant = getAntObject();
		ant.setHeading(ant.getHeading() - 5);

		System.out.println("Changing ant to heading: " + ant.getHeading());

		setChanged();
		notifyObservers();
	}

	/**
	 * 'r' - Change the ant's heading 5 degrees to the right.
	 */
	public void headRight() {
		Ant ant = getAntObject();
		ant.setHeading(ant.getHeading() + 5);

		System.out.println("Changing ant to heading: " + ant.getHeading());

		setChanged();
		notifyObservers();
	}

	/**
	 * 'c' - Tell the game world to set the food consumption rate of the ant.
	 */
	public void setConsumeRate() {
		Random rand = new Random();
		Ant ant = getAntObject();

		ant.setFoodConsumption(rand.nextInt(5));

		System.out.println("Changing ant consumption to: " + ant.getFoodConsumptionRate());

		setChanged();
		notifyObservers();
	}

	/**
	 * '0-9' - Tell the game to 'touch' a flag. Use the static method Dialog.show()
	 * to display a dialog box that allows the user to enter the number on a text
	 * field located on the dialog box.
	 * 
	 * @param flagNumber
	 */
	public void flagTouch(int flagNumber) {
		Ant ant = getAntObject();
		ant.setLastFlagReached(flagNumber);
		
		if (isSound()) {
			flagSound.play();
		}

		if (ant.getLastFlagReached() == 4) {
			System.out.println("You win! Time: " + getGameTick());
			System.exit(0);
		}

		setChanged();
		//notifyObservers();
	}

	/**
	 * 'f' - Pretend to touch a food station. Tell the game world that this
	 * collision has occurred. The effect of colliding with a food station is to
	 * increase the ant's food level by the capacity of the food station (in this
	 * version of the assignment, pick a non-empty food station randomly), reduce
	 * the capacity of the food station to zero, fade the color of the food station
	 * (e.g., change it to light green), and add a new food station with
	 * randomly-specified size and location into the game.
	 */
	public void foodStationTouch(FoodStation touchedFoodStation) {
		// Logic done here because the assignment says "tell the game world that this
		// collision has occurred".
		Ant ant = getAntObject();
		Random rand = new Random();

		ant.registerFoodStation(touchedFoodStation);
		
		//touchedFoodStation.setFoodCapacity(0);

		// Create a new food object in a random position
		// int size, int color, Point location, int foodCapacity
		FoodStation newFoodStation = new FoodStation(rand.nextInt(10) + 40, ColorUtil.rgb(0, 255, 0),
				getRandomLocation(), rand.nextInt(20) + 10);
		addGameObject(newFoodStation);

		System.out.println("Ant food set to: " + ant.getFood());
		System.out.println("New food station at: " + newFoodStation.getLocation().getX() + ", "
				+ newFoodStation.getLocation().getY());

		if (isSound()) {
			foodSound.play();
		}
		
		setChanged();
		//notifyObservers();
	}

	/**
	 * 'g' - Collide with a spider The effect of colliding with a spider is to
	 * decrease the health level of the ant as described above, fade the color of
	 * the ant (i.e., it becomes lighter red - throughout the game, the ant can have
	 * different shades of red), and (if necessary) reduce the speed of the ant so
	 * that above-mentioned speed-limitation rule is enforced. Since currently no
	 * change is introduced to the spider after the collision, it does not matter
	 * which spider is picked.
	 */
	public void spiderTouch() {
		Ant ant = getAntObject();

		System.out.println("Ant collided with spider.");

		// If ant health is 0, take a life
		if (ant.getHealth() - 1 <= 0) {
			lives--;
			ant.takeLife(getLives());
		} else {
			ant.setHealth(ant.getHealth() - 1);
		}
		
		// If sound is on, squeak
		if (isSound()) {
			squeakSound.play();
		}

		setChanged();
		//notifyObservers();
	}

	/**
	 * 't' - A clock tick in the game world has the following effects: (1) Spiders
	 * update their heading as indicated above. (2) all moveable objects are told to
	 * update their positions according to their current heading and speed, and (3)
	 * the ant's food level is reduced by the amount indicated by its
	 * foodConsumptionRate, (4) the elapsed time "game clock" is incremented by one
	 * (the game clock for this assignment is simply a variable which increments by
	 * one with each tick). Note that all commands take immediate effect and not
	 * depend on 't' command (e.g., if 'a' is hit, the ant's speed value would be
	 * increased right away without waiting for the next 't' command to be entered).
	 * 
	 * @param elapsedTime
	 */
	public void tickClock(int elapsedTime) {
		Random rand = new Random();
		GameObject currentObject;
		gameObs.reset();

		// A for loop that applies effects to each object
		while (getGameObs().hasNext()) {
			currentObject = getGameObs().getNext();

			// Spiders update their heading every 50 ticks
			if (currentObject instanceof Spider && (getGameTick()+1)%50 == 0) {
				// To get plus or minus 5, select a random value up to 10 and subtract 5
				((Spider) currentObject).setHeading(rand.nextInt(150) - 75);
			}

			// Moveable objects are told to update their positions according to their
			// current heading and speed
			if (currentObject instanceof Moveable) {
				Moveable toMove = (Moveable) currentObject;
				toMove.moveObject(toMove.getSpeed(), getWorldWidth(), getWorldHeight(), elapsedTime, getWorldPositionX(), getWorldPositionY());
			}

			// Ant's food level is reduced by foodConsumptionRate
			// To make the game playable, only take away from food every few ticks
			if (currentObject instanceof Ant && (getGameTick()+1)%15 == 0) {
				Ant ant = (Ant) currentObject;
				ant.reduceFood();
			}
		}

		// Check if moving caused any collisions
		IIterator obs = getGameObs();
		ArrayList<GameObject> obsList = new ArrayList<>();
		obs.reset();
		while (obs.hasNext()) {
			obsList.add(obs.getNext());
		}
		
		for (int i = 0; i < obsList.size(); i++) {
			GameObject collisionObj = obsList.get(i);
			
			for (int j = 0; j < obsList.size(); j++) {
				GameObject otherObj = obsList.get(j);
				
				// check for collision
				if (otherObj != collisionObj && otherObj instanceof ICollider && collisionObj.collidesWith(otherObj)) {
					if (!otherObj.getCollisionVector().contains(collisionObj)
							&& !collisionObj.getCollisionVector().contains(otherObj)) {
						
						// Trigger the appropriate GameWorld responses
						if (otherObj instanceof Ant && collisionObj instanceof Flag) {
							this.flagTouch(((Flag) collisionObj).getFlagNum());
							System.out.println("Flag responded to.");
						} else if (collisionObj instanceof Ant && otherObj instanceof Flag) {
							this.flagTouch(((Flag) collisionObj).getFlagNum());
							System.out.println("Flag responded to.");
						}
						
						else if (otherObj instanceof Ant && collisionObj instanceof FoodStation) {
							this.foodStationTouch(((FoodStation) collisionObj));
							System.out.println("Food station responded to.");
						} else if (collisionObj instanceof Ant && otherObj instanceof FoodStation) {
							this.foodStationTouch(((FoodStation) otherObj));
							System.out.println("Food station responded to.");
						}
						
						else if (otherObj instanceof Ant && collisionObj instanceof Spider) {
							this.spiderTouch();
							System.out.println("Spider responded to.");
						} else if (collisionObj instanceof Ant && otherObj instanceof Spider) {
							this.spiderTouch();
							System.out.println("Spider responded to.");
						}
						
						// Trigger each object's response and log their connections
						collisionObj.handleCollision(otherObj);
						otherObj.handleCollision(collisionObj);
	
						// For testing
						//System.out.println("CollisionObj: " + collisionObj.toString());
						//System.out.println("otherObj: " + otherObj.toString());
					}
					

				}
			}
		}

		// Game clock updates
		setGameTick(getGameTick() + 1);

		//System.out.println("Game tick: " + getGameTick());

		notifyObservers();
	}

	/**
	 * 'd' - generate a display by outputting lines of text on the console
	 * describing the current game/ant state values. The display should include (1)
	 * the number of lives left, (2) the current clock value (elapsed time), (3) the
	 * highest flag number the ant has reached sequentially so far (i.e.,
	 * lastFlagReached), (4) the ant's current food level (i.e., foodLevel), and (5)
	 * the ant's health level (i.e., healthLevel). All output should be
	 * appropriately labeled in easily readable format.
	 */
	public void updateDisplay() {
		Ant ant = getAntObject();

		System.out.println("====Display===");
		System.out.println(
				"Lives: " + getLives() + ", tick: " + getGameTick() + ", last flag: " + ant.getLastFlagReached() + "\n"
						+ "food: " + ant.getFood() + ", health: " + ant.getHealth() + "\n==============");
	}

	/**
	 * 'm' - Generate a map in the display to show where everything is.
	 */
	public void updateMap() {
		GameObject gameObject;
		gameObs.reset();

		
		System.out.println("=====MAP======");
		while (getGameObs().hasNext()) {
			gameObject = getGameObs().getNext();
			System.out.println(gameObject.toString());
		}
		System.out.println("==============");
	}

	/**
	 * If the user requests to exit, end the program
	 * 
	 * @param inputCommand
	 */
	public void exitGame(char inputCommand) {
		if (inputCommand == 'y') {
			System.out.println("Exiting.");
			System.exit(0);
		} else if (inputCommand == 'n') {
			System.out.println("Continuing.");
		}
	}
	
	public void createSounds() {
		squeakSound = new Sound("Squeak.wav");
		flagSound = new Sound("FlagTouch.wav");
		foodSound = new Sound("Chomp.wav");
		constSound = new BGSound("wind.mp3");
	}
	
	/**
	 * Moves an object to a location.
	 * @param gameObject
	 */
	public void moveObject(GameObject gameObject) {
		Point toMove = new Point();
		int[] location = this.getLastPointClicked();
		
		toMove.setX(location[0]);
		toMove.setY(location[1]);
		
		gameObject.setLocation(toMove);
		
		// For Testing
		System.out.println("Set location to: " + toMove.getX() + ", " + toMove.getY());
	}

	// Getters and setters =======================
	
	public BGSound getBackgroundSound() {
		return constSound;
	}

	/**
	 * Get a random location.
	 * @return Point
	 */
	private Point getRandomLocation() {
		Random rand = new Random();

		float x = rand.nextFloat() * (getWorldWidth());
		float y = rand.nextFloat() * (getWorldHeight());

		setChanged();
		return new Point(x, y);
	}

	public IIterator getGameObs() {
		return gameObs;
	}

	public void setGameObs(IIterator gameObs) {
		setChanged();
		notifyObservers();
		this.gameObs = gameObs;
	}

	public void addGameObject(GameObject object) {
		setChanged();
		objectCollection.add(object);
	}

	public GameObject getGameObject(GameObject object) {
		GameObject current;
		gameObs.reset();

		while (getGameObs().hasNext()) {
			current = getGameObs().getNext();

			if (object.equals(current)) {
				return object;
			}
		}
		System.out.println("No object found: " + object);
		return null;
	}

	public Ant getAntObject() {
		GameObject current;
		gameObs.reset();

		while (getGameObs().hasNext()) {
			current = getGameObs().getNext();

			if (current instanceof Ant) {
				return (Ant) current;
			}
		}

		System.out.println("Ant not found!");
		return null;
	}

	public int getGameTick() {
		return gameTick;
	}

	public void setGameTick(int gameTick) {
		this.gameTick = gameTick;
		setChanged();
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
		setChanged();
	}

	public boolean isSound() {
		return sound;
	}

	public void setSound(boolean sound) {
		this.sound = sound;
		setChanged();
		notifyObservers();
	}

	public float getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(float worldWidth) {
		this.worldWidth = worldWidth;
		setChanged();
	}

	public float getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(float worldHeight) {
		this.worldHeight = worldHeight;
		setChanged();
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
		
		// For testing
		//System.out.println("gw.isPaused: " + isPaused());
	}

	public int[] getLastPointClicked() {
		return lastPointClicked;
	}

	public void setLastPointClicked(int[] lastPointClicked) {
		this.lastPointClicked = lastPointClicked;
	}

	public boolean isSelectedMove() {
		return selectedMove;
	}

	public void setSelectedMove(boolean selectedMove) {
		this.selectedMove = selectedMove;
	}

	public float getWorldPositionX() {
		return worldPositionX;
	}

	public void setWorldPositionX(float worldPositionX) {
		this.worldPositionX = worldPositionX;
	}

	public float getWorldPositionY() {
		return worldPositionY;
	}

	public void setWorldPositionY(float worldPositionY) {
		this.worldPositionY = worldPositionY;
	}

}