package com.mycompany.a2;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Border;

/**
 * A ScoreView to maintain the labels below the tool bar at the top of the page.
 */
public class ScoreView extends Container implements Observer {
	private GameWorld gw;
	private Label scoreLabel;

	/**
	 * Constructor
	 * @param gw
	 */
	public ScoreView(GameWorld gw) {
		this.gw = gw;

		setLayout(new BorderLayout());
		setup();
	}

	/**
	 * Updates the label at the top bar.
	 */
	public void update(Observable o, Object arg) {
		System.out.println("Updating the score...");

		String soundString = "";
		if (gw.isSound()) {
			soundString = "ON";
		} else {
			soundString = "OFF";
		}

		try {
			// code here to update labels from the game/ant state data
			scoreLabel.setText("Time: " + gw.getGameTick() + " Lives Left: " + gw.getLives() + " Last Flag Reached: "
					+ gw.getAntObject().getLastFlagReached() + " Food level: " + gw.getAntObject().getFood()
					+ " Health level: " + gw.getAntObject().getHealth() + " Sound: " + soundString);

		} catch (NullPointerException e) {
			System.out.println("Tried to update score but something was null!");
			e.printStackTrace();
		}
	}

	/**
	 * Creates the label and sets up the text.
	 */
	private void setup() {
		scoreLabel = new Label();

		System.out.println("Setting up labels...");
		this.getAllStyles().setBorder(Border.createLineBorder(3, ColorUtil.BLACK));

		scoreLabel = new Label( "Time: 0 Lives Left: 0"
					+ " Last Flag Reached: 0 Food level: 0" + " Health level: 0 Sound: OFF");

		scoreLabel.getAllStyles().setFgColor(ColorUtil.BLUE);
		scoreLabel.getAllStyles().setAlignment(Component.CENTER);

		this.add(BorderLayout.CENTER, scoreLabel);
	}
}