package com.mycompany.a2;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.Border;
import java.lang.String;

/**
 * Creates a GameWorld, Mapview, Scoreview, as well as all commands.
 * It creates and applies buttons and applies commands to buttons, keybinds, and other menus.
 */
public class Game extends Form {
	private GameWorld gw;
	private MapView mv;
	private ScoreView sv;

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
		
		this.setLayout(new BorderLayout());
		
		this.add(BorderLayout.NORTH, sv);
		this.add(BorderLayout.CENTER, mv);
		
		// Commands that go in buttons (at least)
		AccelerateCommand accelCommand = new AccelerateCommand("Accelerate", gw);
		LeftCommand leftCommand = new LeftCommand("Left", gw);
		BrakeCommand brakeCommand = new BrakeCommand("Brake", gw);
		RightCommand rightCommand = new RightCommand("Right", gw);
		CollideFlagCommand collideFlagCommand = new CollideFlagCommand("Collide with Flag", gw);
		CollideSpiderCommand collideSpiderCommand = new CollideSpiderCommand("Collide with Spider", gw);
		CollideFoodStationCommand collideFoodStationCommand = new CollideFoodStationCommand("Collide with Food Stations", gw);
		TickCommand tickCommand = new TickCommand("Tick", gw);
		
		// Commands only for key bindings
		SetFoodConsumptionCommand setFoodConsumptionCommand = new SetFoodConsumptionCommand("Set food consumption", gw);
		addKeyListener('c', setFoodConsumptionCommand);
		
		// Commands only for the side menu
		SoundCommand soundCommand = new SoundCommand("Sound", gw);
		AboutCommand aboutCommand = new AboutCommand("About");
		ExitCommand exitCommand = new ExitCommand("Exit", gw);
		HelpCommand helpCommand = new HelpCommand("Help");
		
		this.add(BorderLayout.WEST, addWestContainer(accelCommand, leftCommand));
        this.add(BorderLayout.EAST, addEastContainer(brakeCommand, rightCommand));
        this.add(BorderLayout.SOUTH, addSouthContainer(collideFlagCommand, collideSpiderCommand, collideFoodStationCommand, tickCommand));
        
        Toolbar toolbar = new Toolbar();
        this.setToolbar(toolbar);
        setupToolbar(toolbar, accelCommand, soundCommand, aboutCommand, exitCommand, helpCommand);
        
		this.show();
		
		// code here to query MapView's width and height and set them as world's width and height
		gw.setWorldWidth(mv.getWidth());
		gw.setWorldHeight(mv.getHeight());
		System.out.println("World width: " + gw.getWorldWidth() + ", height: " + gw.getWorldWidth());
		
		gw.init();
	}

	/**
	 * Creates buttons and applies commands to the west container.
	 * @param accelCommand
	 * @param leftCommand
	 * @return
	 */
	private Component addWestContainer(AccelerateCommand accelCommand, LeftCommand leftCommand) {
		Container westContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		westContainer.getAllStyles().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		Button accelerateButton = createButton("Accelerate");
		accelerateButton.setCommand(accelCommand);
		addKeyListener('a', accelCommand);
		
		Button leftButton = createButton("Left");
		leftButton.setCommand(leftCommand);
		addKeyListener('l', leftCommand);
		
		westContainer.addAll(accelerateButton, leftButton);
		return westContainer;
	}

	/**
	 * Creates buttons and applies commands to the east container.
	 * @param brakeCommand
	 * @param rightCommand
	 * @return
	 */
	private Component addEastContainer(BrakeCommand brakeCommand, RightCommand rightCommand) {
		Container eastContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		eastContainer.getAllStyles().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		Button breakButton = createButton("Brake");
		breakButton.setCommand(brakeCommand);
		addKeyListener('b', brakeCommand);
		
		Button rightButton = createButton("Right");
		rightButton.setCommand(rightCommand);
		addKeyListener('r', rightCommand);
		
		eastContainer.addAll(breakButton, rightButton);
		return eastContainer;
	}

	/**
	 * Creates buttons and applies commands to the south container.
	 * @param collideFlagCommand
	 * @param collideSpiderCommand
	 * @param collideFoodStationCommand
	 * @param tickCommand
	 * @return
	 */
	private Component addSouthContainer(CollideFlagCommand collideFlagCommand, CollideSpiderCommand collideSpiderCommand, CollideFoodStationCommand collideFoodStationCommand, TickCommand tickCommand) {
		Container southContainer = new Container(new FlowLayout(Container.CENTER));
		southContainer.getAllStyles().setBorder(Border.createLineBorder(3,ColorUtil.BLACK));
		
		Button collideFlagButton = createButton("Collide with Flag");
		collideFlagButton.setCommand(collideFlagCommand);
		// Flag has no key binding
		
		Button collideSpiderButton = createButton("Collide with Spider");
		collideSpiderButton.setCommand(collideSpiderCommand);
		addKeyListener('g', collideSpiderCommand);
		
		Button collideFoodStationButton = createButton("Collide with Food Stations");
		collideFoodStationButton.setCommand(collideFoodStationCommand);
		addKeyListener('f', collideFoodStationCommand);
		
		Button tickButton = createButton("Tick");
		tickButton.setCommand(tickCommand);
		addKeyListener('t', tickCommand);
		
		
		southContainer.addAll(collideFlagButton, collideSpiderButton, collideFoodStationButton, tickButton);
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
	private void setupToolbar(Toolbar toolbar, AccelerateCommand accelCommand, SoundCommand soundCommand, AboutCommand aboutCommand, ExitCommand exitCommand, HelpCommand helpCommand) {
        toolbar.setTitle("Avoid-It Game");
        
        // Left menu
        toolbar.addCommandToSideMenu(accelCommand);
        
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
}