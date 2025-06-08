package com.mycompany.a1;

import com.codename1.ui.Form;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import java.lang.String;

/**
 * Creates a GameWorld and handles incoming commands.
 */
public class Game extends Form {
	private GameWorld gw;
	private boolean exitCommanded = false;

	/**
	 * Create a new world.
	 */
	public Game() {
		System.out.println("Creating new world.");
		gw = new GameWorld();
		gw.init();
		play();
	}

	/**
	 * Listen to user commands.
	 * 'a' - Accelerate the ant.
	 * 'b' - Slow the ant.
	 * 'l' - Change the ant's heading to the left.
	 * 'r' - Change the ant's heading to the right.
	 * 'c' - Set the ant's consume rate to 5.
	 * '1-9' - Trigger the ant to touch a flag.
	 * 'f' - Trigger the ant to touch a food station.
	 * 'g' - Trigger the ant to touch a spider.
	 * 't' - Move the game clock forward 1 tick.
	 * 'd' - Display various attributes of the ant.
	 * 'm' - Display the locations and statuses of all objects.
	 * 'x' - Ask the user if they want to exit the game. 'y' for yes, 'n' for no.
	 */
	private void play() {
		Label myLabel = new Label("Enter a Command:");
		this.addComponent(myLabel);
		final TextField myTextField = new TextField();
		this.addComponent(myTextField);
		this.show();
		myTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String sCommand = myTextField.getText().toString();
				myTextField.clear();
				if (sCommand.length() != 0) {
					char inputCommand = sCommand.charAt(0);
					switch (sCommand.charAt(0)) {
					case 'a':
						gw.accelerate();
						break;

					case 'b':
						gw.brake();
						break;
					
					case 'l':
						gw.headLeft();
						break;
						
					case 'r':
						gw.headRight();
						break;
					
					case 'c':
						gw.setConsumeRate();
						break;
						
					case '1': case '2': case '3': case '4': 
                    case '5': case '6': case '7': case '8': case '9':
                    	// Subtract 48 in ASCII (I was having trouble getting the value of the digit)
                    	int flagToHit = inputCommand - '0';
                    	gw.flagTouch(flagToHit);
                    	break;
                    
                    case 'f':
                    	gw.foodStationTouch();
                    	break;
                    
                    case 'g':
                    	gw.spiderTouch();
                    	break;
                    	
                    case 't':
                    	gw.tickClock();
                    	break;
                    	
                    case 'd':
                    	gw.updateDisplay();
                    	break;
                    	
                    case 'm':
                    	gw.updateMap();
                    	break;
                    	
                    case 'x':
                    	exitCommanded = true;
                		System.out.println("Are you sure you would like to exit the program? y/n.");
                    	break;
                    	
                    case 'y': case 'n':
                    	if (exitCommanded == true) {
                        	gw.exitGame(inputCommand);
                    	}
                    	
                    default:
                    	System.out.println("Invalid input!");
                    	break;
                    	
					} // switch
				} // If statement
			} // actionPerformed
		} // new ActionListener()
		); // addActionListener
	}
}
