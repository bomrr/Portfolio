package com.mycompany.a3;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.util.UITimer;

import java.lang.String;
import java.util.Vector;

/**
 * Creates a GameWorld, Mapview, Scoreview, as well as all commands.
 * It creates and applies buttons and applies commands to buttons, keybinds, and other menus.
 */
public class Game extends Form implements Runnable {
	private GameWorld gw;
	private MapView mv;
	private ScoreView sv;
	private boolean isPaused = false;
	
	// Commands/buttons that can only be used when the game is unpaused
	private Vector<Button> unpausedOnlyButtons = new Vector<>();
	private Vector<Command> unpausedOnlyCommands = new Vector<>();
	
	// Commands/buttons that can only be used when the game is paused
	private Vector<Button> pausedOnlyButtons = new Vector<>();
	private Vector<Command> pausedOnlyCommands = new Vector<>();
	
	// Key-paired commands that need to be accessible to turn off
	private SetFoodConsumptionCommand setFoodConsumptionCommand;
	private AccelerateCommand accelCommand;
	private LeftCommand leftCommand;
	private BrakeCommand brakeCommand;
	private RightCommand rightCommand;
	
	// Toolbar
	private Toolbar toolbar;
	

	/**
	 * Create a new world, mapview, and scoreview.
	 */
	public Game() {
		System.out.println("Creating new world.");
		gw = new GameWorld(); // create "Observable" GameWorld
		mv = new MapView(gw); // create an "Observer" for the map
		sv = new ScoreView(gw); // create an "Observer" for the game/ant state data
		gw.addObserver(mv); // register the map observer
		gw.addObserver(sv); // register the score observer
		
		System.out.println("Loading sounds...");
		gw.createSounds();
		
		this.setLayout(new BorderLayout());
		
		this.add(BorderLayout.NORTH, sv);
		this.add(BorderLayout.CENTER, mv);
		
		// Commands that go in buttons (at least)
		accelCommand = new AccelerateCommand("Accelerate", gw);
		unpausedOnlyCommands.add(accelCommand);
		leftCommand = new LeftCommand("Left", gw);
		unpausedOnlyCommands.add(leftCommand);
		brakeCommand = new BrakeCommand("Brake", gw);
		unpausedOnlyCommands.add(brakeCommand);
		rightCommand = new RightCommand("Right", gw);
		unpausedOnlyCommands.add(rightCommand);
		
		// Commands only for key bindings
		setFoodConsumptionCommand = new SetFoodConsumptionCommand("Set food consumption", gw);
		addKeyListener('c', setFoodConsumptionCommand);
		unpausedOnlyCommands.add(setFoodConsumptionCommand);
		
		// Commands only for the side menu
		// These commands are always on (I think)
		SoundCommand soundCommand = new SoundCommand("Sound", gw, this);
		AboutCommand aboutCommand = new AboutCommand("About");
		ExitCommand exitCommand = new ExitCommand("Exit", gw);
		HelpCommand helpCommand = new HelpCommand("Help");
		
		this.add(BorderLayout.WEST, addWestContainer());
        this.add(BorderLayout.EAST, addEastContainer());
        
        toolbar = new Toolbar();
        this.setToolbar(toolbar);
        setupToolbar(soundCommand, aboutCommand, exitCommand, helpCommand);
        
		// The pause button is created earlier so it can be updated properly.
		Button pauseButton = createButton("Pause");
		
		// Pause command and the clock timer
		// Create a UI Timer to run the animations every frame
		UITimer clockTimer = new UITimer(this);
		PauseCommand pauseCommand = new PauseCommand("Pause", gw, clockTimer, this, pauseButton);
		isPaused = pauseCommand.isPaused();
		pauseButton.setCommand(pauseCommand);
		
		// Position command
		PositionCommand positionCommand = new PositionCommand("Position", gw);
		pausedOnlyCommands.add(positionCommand);
		
        this.add(BorderLayout.SOUTH, addSouthContainer(pauseButton, positionCommand));
        
		this.show();
		
		System.out.println("Loading game world...");
		updateButtons();
		revalidate();
		
		// For testing
		//System.out.println("Mapview before query: " + mv.getWidth() + ", " + mv.getHeight());
		///System.out.println("Mapview position relative to compoenents: " + mv.getAbsoluteX() + ", " + mv.getAbsoluteY());
		
		// code here to query MapView's width and height and set them as world's width and height
		gw.setWorldWidth(mv.getWidth());
		gw.setWorldHeight(mv.getHeight());
		System.out.println("World width: " + gw.getWorldWidth() + ", height: " + gw.getWorldHeight());
		
		gw.setWorldPositionX(mv.getAbsoluteX());
		gw.setWorldPositionY(mv.getAbsoluteY());
		System.out.println("World position: " + gw.getWorldPositionX() + ", " + gw.getWorldPositionY());
		
		gw.init();
		
		System.out.println("Starting game.");
		clockTimer.schedule(20, true, this);
	}

	/**
	 * Creates buttons and applies commands to the west container.
	 * @param accelCommand
	 * @param leftCommand
	 * @return
	 */
	private Component addWestContainer() {
		Container westContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		westContainer.getAllStyles().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		Button accelerateButton = createButton("Accelerate");
		accelerateButton.setCommand(accelCommand);
		addKeyListener('a', accelCommand);
		unpausedOnlyButtons.add(accelerateButton);
		
		Button leftButton = createButton("Left");
		leftButton.setCommand(leftCommand);
		addKeyListener('l', leftCommand);
		unpausedOnlyButtons.add(leftButton);
		
		westContainer.addAll(accelerateButton, leftButton);
		return westContainer;
	}

	/**
	 * Creates buttons and applies commands to the east container.
	 * @param brakeCommand
	 * @param rightCommand
	 * @return
	 */
	private Component addEastContainer() {
		Container eastContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		eastContainer.getAllStyles().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		Button breakButton = createButton("Brake");
		breakButton.setCommand(brakeCommand);
		addKeyListener('b', brakeCommand);
		unpausedOnlyButtons.add(breakButton);
		
		Button rightButton = createButton("Right");
		rightButton.setCommand(rightCommand);
		addKeyListener('r', rightCommand);
		unpausedOnlyButtons.add(rightButton);
		
		eastContainer.addAll(breakButton, rightButton);
		return eastContainer;
	}

	/**
	 * Creates buttons and applies commands to the south container.
	 * @param positionCommand 
	 * @param collideFlagCommand
	 * @param 
	 * @param collideFoodStationCommand
	 * @param tickCommand
	 * @return
	 */
	private Component addSouthContainer(Button pauseButton, PositionCommand positionCommand) {
		Container southContainer = new Container(new FlowLayout(Container.CENTER));
		southContainer.getAllStyles().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		// The pause button is created earlier so it can be updated properly.
		
		Button positionButton = createButton("Position");
		positionButton.setCommand(positionCommand);
		pausedOnlyButtons.add(positionButton);
		
		southContainer.addAll(pauseButton, positionButton);
		return southContainer;
	}
	
	/**
	 * Creates buttons and applies commands to the tool bar left and right menus.
	 * @param toolbar
	 * @param accelCommand
	 * @param soundCommand
	 * @param aboutCommand
	 * @param exitCommand
	 * @param helpCommand
	 */
	private void setupToolbar(SoundCommand soundCommand, AboutCommand aboutCommand, ExitCommand exitCommand, HelpCommand helpCommand) {
        toolbar.setTitle("Avoid-It Game");
        
        // Left menu
        //toolbar.addCommandToSideMenu(accelCommand);
        unpausedOnlyCommands.add(accelCommand);
        
        CheckBox checkBox = new CheckBox();
        checkBox.getAllStyles().setBgTransparency(255);
        checkBox.getAllStyles().setBgColor(ColorUtil.LTGRAY);
		checkBox.setCommand(soundCommand);
		toolbar.addComponentToSideMenu(checkBox);
		
		toolbar.addCommandToSideMenu(aboutCommand);
		toolbar.addCommandToSideMenu(exitCommand);
		
		// Right menu
		toolbar.addCommandToRightBar(helpCommand);
	}
	
	/**
	 * Runs every frame while active.
	 */
	@Override
	public void run() {
		gw.tickClock(20);
	}
	
	/**
	 * Creates a button with a supplied text label.
	 * @param text
	 * @return
	 */
	private Button createButton(String text) {
		Button button = new Button(text);
		
		button.getUnselectedStyle().setBgTransparency(255);
		button.getUnselectedStyle().setBgColor(ColorUtil.BLUE);
		button.getUnselectedStyle().setFgColor(ColorUtil.WHITE);
		
		button.getUnselectedStyle().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		// Set the margins and padding of the buttons
		button.getAllStyles().setAlignment(Component.CENTER);
		button.getAllStyles().setMargin(Component.TOP, 0);
		button.getAllStyles().setMargin(Component.BOTTOM, 0);
		button.getAllStyles().setPadding(Component.TOP, 8);
		button.getAllStyles().setPadding(Component.BOTTOM, 8);
		
		return button;
	}
	
	private void updateButtons() {
		if (isPaused()) {
			for (Button b : unpausedOnlyButtons) b.setEnabled(false);
			for (Command c : unpausedOnlyCommands) c.setEnabled(false);

			for (Button b : pausedOnlyButtons) b.setEnabled(true);
			for (Command c : pausedOnlyCommands) c.setEnabled(true);
			
			removeKeyListener('c', setFoodConsumptionCommand);
			removeKeyListener('a', accelCommand);
			removeKeyListener('l', leftCommand);
			removeKeyListener('b', brakeCommand);
			removeKeyListener('r', rightCommand);
			
			toolbar.removeCommand(accelCommand);
			
		} else {
			for (Button b : unpausedOnlyButtons) b.setEnabled(true);
			for (Command c : unpausedOnlyCommands) c.setEnabled(true);

			for (Button b : pausedOnlyButtons) b.setEnabled(false);
			for (Command c : pausedOnlyCommands) c.setEnabled(false);
			
			addKeyListener('c', setFoodConsumptionCommand);
			addKeyListener('a', accelCommand);
			addKeyListener('l', leftCommand);
			addKeyListener('b', brakeCommand);
			addKeyListener('r', rightCommand);
			
			toolbar.addCommandToLeftSideMenu(accelCommand);
		}
		this.revalidate();
	}

	public boolean isPaused() {
		return isPaused;
	}
	
	public void setPaused(boolean b) {
		isPaused = b;
		updateButtons();
	}
	
}